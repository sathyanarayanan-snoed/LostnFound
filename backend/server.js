const express = require('express');
const cors = require('cors');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
const sqlite3 = require('sqlite3').verbose();
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const { v4: uuidv4 } = require('uuid');

const PORT = process.env.PORT || 8080;
const JWT_SECRET = process.env.JWT_SECRET || 'lostnfound_secret_key_2026';
const ONE_HOUR_MS = 3600000;
const SEVEN_DAYS_MS = 7 * 24 * 60 * 60 * 1000;
const MAX_POSTS_PER_HOUR = 5;

const dataDir = path.join(__dirname, 'data');
const uploadsDir = path.join(__dirname, 'uploads');
if (!fs.existsSync(dataDir)) fs.mkdirSync(dataDir, { recursive: true });
if (!fs.existsSync(uploadsDir)) fs.mkdirSync(uploadsDir, { recursive: true });

const db = new sqlite3.Database(path.join(dataDir, 'lostnfound.db'));

db.serialize(() => {
  db.run(`CREATE TABLE IF NOT EXISTS users (
    uid TEXT PRIMARY KEY,
    email TEXT UNIQUE,
    password_hash TEXT,
    display_name TEXT,
    created_at INTEGER,
    post_count INTEGER DEFAULT 0,
    last_post_time INTEGER DEFAULT 0,
    fcm_token TEXT
  )`);

  db.run(`CREATE TABLE IF NOT EXISTS found_items (
    id TEXT PRIMARY KEY,
    reporter_id TEXT,
    finder_name TEXT,
    finder_contact TEXT,
    place_found TEXT,
    description TEXT,
    category TEXT,
    image_url TEXT,
    latitude REAL,
    longitude REAL,
    reported_at INTEGER,
    expires_at INTEGER,
    claimed INTEGER DEFAULT 0,
    status TEXT DEFAULT 'active'
  )`);

  db.run(`CREATE TABLE IF NOT EXISTS lost_items (
    id TEXT PRIMARY KEY,
    reporter_id TEXT,
    owner_name TEXT,
    owner_contact TEXT,
    description TEXT,
    category TEXT,
    image_url TEXT,
    proof_of_ownership TEXT,
    proof_image_url TEXT,
    latitude REAL,
    longitude REAL,
    lost_date INTEGER,
    reported_at INTEGER,
    expires_at INTEGER,
    claimed INTEGER DEFAULT 0,
    status TEXT DEFAULT 'active',
    flagged INTEGER DEFAULT 0
  )`);
});

const app = express();
app.use(cors());
app.use(express.json());
app.use('/uploads', express.static(uploadsDir));

const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, uploadsDir),
  filename: (req, file, cb) => {
    const ext = path.extname(file.originalname) || '.jpg';
    cb(null, `${uuidv4()}${ext}`);
  }
});
const upload = multer({ storage, limits: { fileSize: 15 * 1024 * 1024 } });

function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) return res.status(401).json({ error: 'Authentication required' });
  jwt.verify(token, JWT_SECRET, (err, user) => {
    if (err) return res.status(403).json({ error: 'Invalid or expired token' });
    req.user = user;
    next();
  });
}

function checkRateLimit(userId, callback) {
  const now = Date.now();
  db.get('SELECT post_count, last_post_time FROM users WHERE uid = ?', [userId], (err, row) => {
    if (err || !row) return callback(null, true);
    if (now - row.last_post_time > ONE_HOUR_MS) {
      db.run('UPDATE users SET post_count = 1, last_post_time = ? WHERE uid = ?', [now, userId], () => {
        callback(null, true);
      });
    } else if (row.post_count < MAX_POSTS_PER_HOUR) {
      db.run('UPDATE users SET post_count = post_count + 1 WHERE uid = ?', [userId], () => {
        callback(null, true);
      });
    } else {
      callback(null, false);
    }
  });
}

app.get('/health', (req, res) => {
  res.json({ status: 'ok', timestamp: Date.now() });
});

app.post('/api/auth/signup', (req, res) => {
  const { email, password, displayName } = req.body;
  if (!email || !password) {
    return res.status(400).json({ error: 'Email and password are required' });
  }
  const uid = uuidv4();
  const passwordHash = bcrypt.hashSync(password, 10);
  const now = Date.now();

  const query = `INSERT INTO users (uid, email, password_hash, display_name, created_at, post_count, last_post_time)
                 VALUES (?, ?, ?, ?, ?, 0, 0)`;
  db.run(query, [uid, email, passwordHash, displayName || email.split('@')[0], now], function(err) {
    if (err) {
      if (err.message.includes('UNIQUE')) {
        return res.status(409).json({ error: 'Account already exists with this email' });
      }
      return res.status(500).json({ error: err.message });
    }
    const token = jwt.sign({ uid, email }, JWT_SECRET, { expiresIn: '30d' });
    res.json({ token, user: { uid, email, displayName: displayName || email.split('@')[0] } });
  });
});

app.post('/api/auth/login', (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ error: 'Email and password are required' });
  }

  db.get('SELECT * FROM users WHERE email = ?', [email], (err, user) => {
    if (err) return res.status(500).json({ error: err.message });
    if (!user || !bcrypt.compareSync(password, user.password_hash)) {
      return res.status(401).json({ error: 'Invalid credentials. Please verify your email and password.' });
    }
    const token = jwt.sign({ uid: user.uid, email: user.email }, JWT_SECRET, { expiresIn: '30d' });
    res.json({
      token,
      user: {
        uid: user.uid,
        email: user.email,
        displayName: user.display_name
      }
    });
  });
});

app.post('/api/auth/reset-password', (req, res) => {
  const { email } = req.body;
  if (!email) return res.status(400).json({ error: 'Email is required' });
  res.json({ success: true, message: 'Password reset link sent to your email' });
});

app.post('/api/upload', upload.single('file'), (req, res) => {
  if (!req.file) return res.status(400).json({ error: 'No file uploaded' });
  const host = req.get('host');
  const protocol = req.protocol;
  const fileUrl = `${protocol}://${host}/uploads/${req.file.filename}`;
  res.json({ url: fileUrl });
});

app.get('/api/items/found', (req, res) => {
  const { query, category, location, dateFrom, dateTo } = req.query;
  const now = Date.now();
  let sql = 'SELECT * FROM found_items WHERE status = "active" AND expires_at > ?';
  const params = [now];

  if (category) {
    sql += ' AND category = ?';
    params.push(category);
  }
  if (query) {
    sql += ' AND (description LIKE ? OR place_found LIKE ? OR finder_name LIKE ?)';
    params.push(`%${query}%`, `%${query}%`, `%${query}%`);
  }
  if (location) {
    sql += ' AND place_found LIKE ?';
    params.push(`%${location}%`);
  }
  if (dateFrom) {
    sql += ' AND reported_at >= ?';
    params.push(Number(dateFrom));
  }
  if (dateTo) {
    sql += ' AND reported_at <= ?';
    params.push(Number(dateTo));
  }

  sql += ' ORDER BY reported_at DESC LIMIT 50';

  db.all(sql, params, (err, rows) => {
    if (err) return res.status(500).json({ error: err.message });
    const items = rows.map(r => ({
      id: r.id,
      reporterId: r.reporter_id,
      finderName: r.finder_name,
      finderContact: r.finder_contact,
      placeFound: r.place_found,
      description: r.description,
      category: r.category,
      imageUrl: r.image_url || '',
      latitude: r.latitude,
      longitude: r.longitude,
      reportedAt: r.reported_at,
      expiresAt: r.expires_at,
      claimed: Boolean(r.claimed),
      status: r.status
    }));
    res.json(items);
  });
});

app.get('/api/items/lost', (req, res) => {
  const { query, category, location, dateFrom, dateTo } = req.query;
  const now = Date.now();
  let sql = 'SELECT * FROM lost_items WHERE status = "active" AND expires_at > ?';
  const params = [now];

  if (category) {
    sql += ' AND category = ?';
    params.push(category);
  }
  if (query) {
    sql += ' AND (description LIKE ? OR owner_name LIKE ?)';
    params.push(`%${query}%`, `%${query}%`);
  }
  if (location) {
    sql += ' AND description LIKE ?';
    params.push(`%${location}%`);
  }
  if (dateFrom) {
    sql += ' AND reported_at >= ?';
    params.push(Number(dateFrom));
  }
  if (dateTo) {
    sql += ' AND reported_at <= ?';
    params.push(Number(dateTo));
  }

  sql += ' ORDER BY reported_at DESC LIMIT 50';

  db.all(sql, params, (err, rows) => {
    if (err) return res.status(500).json({ error: err.message });
    const items = rows.map(r => ({
      id: r.id,
      reporterId: r.reporter_id,
      ownerName: r.owner_name,
      ownerContact: r.owner_contact,
      description: r.description,
      category: r.category,
      imageUrl: r.image_url || '',
      proofOfOwnership: r.proof_of_ownership || '',
      proofImageUrl: r.proof_image_url || '',
      latitude: r.latitude,
      longitude: r.longitude,
      lostDate: r.lost_date,
      reportedAt: r.reported_at,
      expiresAt: r.expires_at,
      claimed: Boolean(r.claimed),
      status: r.status,
      flagged: Boolean(r.flagged)
    }));
    res.json(items);
  });
});

app.post('/api/items/found', (req, res) => {
  const item = req.body;
  const reporterId = item.reporterId || 'anonymous';

  checkRateLimit(reporterId, (err, allowed) => {
    if (!allowed) {
      return res.status(429).json({ error: 'Post limit reached. Maximum 5 posts per hour.' });
    }

    const id = uuidv4();
    const now = Date.now();
    const expiresAt = now + SEVEN_DAYS_MS;

    const sql = `INSERT INTO found_items 
      (id, reporter_id, finder_name, finder_contact, place_found, description, category, image_url, latitude, longitude, reported_at, expires_at, claimed, status)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 'active')`;

    db.run(sql, [
      id, reporterId, item.finderName || '', item.finderContact || '',
      item.placeFound || '', item.description || '', item.category || 'OTHER',
      item.imageUrl || '', item.latitude || null, item.longitude || null,
      now, expiresAt
    ], function(err) {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ id });
    });
  });
});

app.post('/api/items/lost', (req, res) => {
  const item = req.body;
  const reporterId = item.reporterId || 'anonymous';

  checkRateLimit(reporterId, (err, allowed) => {
    if (!allowed) {
      return res.status(429).json({ error: 'Post limit reached. Maximum 5 posts per hour.' });
    }

    const id = uuidv4();
    const now = Date.now();
    const expiresAt = now + SEVEN_DAYS_MS;
    const proof = item.proofOfOwnership || '';
    const flagged = proof.length < 20 ? 1 : 0;

    const sql = `INSERT INTO lost_items 
      (id, reporter_id, owner_name, owner_contact, description, category, image_url, proof_of_ownership, proof_image_url, latitude, longitude, lost_date, reported_at, expires_at, claimed, status, flagged)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 'active', ?)`;

    db.run(sql, [
      id, reporterId, item.ownerName || '', item.ownerContact || '',
      item.description || '', item.category || 'OTHER', item.imageUrl || '',
      proof, item.proofImageUrl || '', item.latitude || null, item.longitude || null,
      item.lostDate || now, now, expiresAt, flagged
    ], function(err) {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ id });
    });
  });
});

app.get('/api/items/found/:id', (req, res) => {
  db.get('SELECT * FROM found_items WHERE id = ?', [req.params.id], (err, r) => {
    if (err) return res.status(500).json({ error: err.message });
    if (!r) return res.status(404).json({ error: 'Item not found' });
    res.json({
      id: r.id,
      reporterId: r.reporter_id,
      finderName: r.finder_name,
      finderContact: r.finder_contact,
      placeFound: r.place_found,
      description: r.description,
      category: r.category,
      imageUrl: r.image_url || '',
      latitude: r.latitude,
      longitude: r.longitude,
      reportedAt: r.reported_at,
      expiresAt: r.expires_at,
      claimed: Boolean(r.claimed),
      status: r.status
    });
  });
});

app.get('/api/items/lost/:id', (req, res) => {
  db.get('SELECT * FROM lost_items WHERE id = ?', [req.params.id], (err, r) => {
    if (err) return res.status(500).json({ error: err.message });
    if (!r) return res.status(404).json({ error: 'Item not found' });
    res.json({
      id: r.id,
      reporterId: r.reporter_id,
      ownerName: r.owner_name,
      ownerContact: r.owner_contact,
      description: r.description,
      category: r.category,
      imageUrl: r.image_url || '',
      proofOfOwnership: r.proof_of_ownership || '',
      proofImageUrl: r.proof_image_url || '',
      latitude: r.latitude,
      longitude: r.longitude,
      lostDate: r.lost_date,
      reportedAt: r.reported_at,
      expiresAt: r.expires_at,
      claimed: Boolean(r.claimed),
      status: r.status,
      flagged: Boolean(r.flagged)
    });
  });
});

app.put('/api/items/:type/:id/claim', (req, res) => {
  const table = req.params.type === 'lost' ? 'lost_items' : 'found_items';
  db.run(`UPDATE ${table} SET claimed = 1, status = 'claimed' WHERE id = ?`, [req.params.id], function(err) {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true });
  });
});

app.delete('/api/items/:type/:id', (req, res) => {
  const table = req.params.type === 'lost' ? 'lost_items' : 'found_items';
  db.run(`DELETE FROM ${table} WHERE id = ?`, [req.params.id], function(err) {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ success: true });
  });
});

app.get('/api/users/:uid/items', (req, res) => {
  const uid = req.params.uid;
  db.all('SELECT * FROM found_items WHERE reporter_id = ? ORDER BY reported_at DESC', [uid], (err, found) => {
    if (err) return res.status(500).json({ error: err.message });
    db.all('SELECT * FROM lost_items WHERE reporter_id = ? ORDER BY reported_at DESC', [uid], (err2, lost) => {
      if (err2) return res.status(500).json({ error: err2.message });
      res.json({
        found: (found || []).map(r => ({
          id: r.id,
          reporterId: r.reporter_id,
          finderName: r.finder_name,
          finderContact: r.finder_contact,
          placeFound: r.place_found,
          description: r.description,
          category: r.category,
          imageUrl: r.image_url || '',
          latitude: r.latitude,
          longitude: r.longitude,
          reportedAt: r.reported_at,
          expiresAt: r.expires_at,
          claimed: Boolean(r.claimed),
          status: r.status
        })),
        lost: (lost || []).map(r => ({
          id: r.id,
          reporterId: r.reporter_id,
          ownerName: r.owner_name,
          ownerContact: r.owner_contact,
          description: r.description,
          category: r.category,
          imageUrl: r.image_url || '',
          proofOfOwnership: r.proof_of_ownership || '',
          proofImageUrl: r.proof_image_url || '',
          latitude: r.latitude,
          longitude: r.longitude,
          lostDate: r.lost_date,
          reportedAt: r.reported_at,
          expiresAt: r.expires_at,
          claimed: Boolean(r.claimed),
          status: r.status,
          flagged: Boolean(r.flagged)
        }))
      });
    });
  });
});

app.listen(PORT, '0.0.0.0', () => {
  process.stdout.write(`Server listening on port ${PORT}\n`);
});
