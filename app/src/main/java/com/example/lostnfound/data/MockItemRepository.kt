package com.example.lostnfound.data

import com.example.lostnfound.domain.model.FoundItem
import com.example.lostnfound.domain.model.ItemCategory
import com.example.lostnfound.domain.model.LostItem
import com.example.lostnfound.domain.model.SearchFilters
import kotlinx.coroutines.delay

class MockItemRepository : ItemRepository {
    private val foundItems = mutableListOf(
        FoundItem(
            id = "f1",
            finderName = "Arjun Kumar",
            finderContact = "arjun.k@campus.edu.in",
            placeFound = "Central Library Hall B",
            description = "Hydroflask 32oz Water Bottle (Cobalt Blue with sticker pack)",
            category = ItemCategory.ACCESSORIES.name,
            imageUrl = "https://images.unsplash.com/photo-1602143301015-7121f0045f22?auto=format&fit=crop&q=80&w=800",
            latitude = 12.9918,
            longitude = 80.2335,
            reportedAt = System.currentTimeMillis() - 1800000 // 30m ago
        ),
        FoundItem(
            id = "f2",
            finderName = "Priya Sharma",
            finderContact = "priya.s@campus.edu.in",
            placeFound = "Student Activity Center (SAC) Cafeteria",
            description = "Silver Apple MacBook Air M2 in grey felt sleeve",
            category = ItemCategory.ELECTRONICS.name,
            imageUrl = "https://images.unsplash.com/photo-1611186871348-b1ec696e523b?auto=format&fit=crop&q=80&w=800",
            latitude = 12.9902,
            longitude = 80.2341,
            reportedAt = System.currentTimeMillis() - 7200000 // 2h ago
        ),
        FoundItem(
            id = "f3",
            finderName = "Rahul Verma",
            finderContact = "rahul.v@campus.edu.in",
            placeFound = "Near Gajendra Circle Fountain",
            description = "Black leather bifold wallet with campus ID card inside",
            category = ItemCategory.DOCUMENTS.name,
            imageUrl = "https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&q=80&w=800",
            latitude = 12.9912,
            longitude = 80.2325,
            reportedAt = System.currentTimeMillis() - 14400000 // 4h ago
        ),
        FoundItem(
            id = "f4",
            finderName = "Sneha Patel",
            finderContact = "sneha.p@campus.edu.in",
            placeFound = "Open Air Theatre (OAT) Row F",
            description = "White AirPods Pro 2nd Gen case with Spigen rugged lock",
            category = ItemCategory.ELECTRONICS.name,
            imageUrl = "https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?auto=format&fit=crop&q=80&w=800",
            latitude = 12.9898,
            longitude = 80.2352,
            reportedAt = System.currentTimeMillis() - 86400000 // 1d ago
        ),
        FoundItem(
            id = "f5",
            finderName = "Vikram Reddy",
            finderContact = "vikram.r@campus.edu.in",
            placeFound = "Department of Computer Science Lab 202",
            description = "Casio Edifice Chronograph Stainless Steel Watch",
            category = ItemCategory.ACCESSORIES.name,
            imageUrl = "https://images.unsplash.com/photo-1524805444758-089113d48a6d?auto=format&fit=crop&q=80&w=800",
            latitude = 12.9930,
            longitude = 80.2318,
            reportedAt = System.currentTimeMillis() - 172800000 // 2d ago
        ),
        FoundItem(
            id = "f6",
            finderName = "Ananya Nair",
            finderContact = "ananya.n@campus.edu.in",
            placeFound = "Central Library 2nd Floor Quiet Zone",
            description = "Ray-Ban Original Wayfarer Sunglasses in tan leather case",
            category = ItemCategory.ACCESSORIES.name,
            imageUrl = "https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&q=80&w=800",
            latitude = 12.9921,
            longitude = 80.2339,
            reportedAt = System.currentTimeMillis() - 259200000 // 3d ago
        )
    )

    private val lostItems = mutableListOf(
        LostItem(
            id = "l1",
            ownerName = "Sarah Wilson",
            ownerContact = "sarah.w@campus.edu.in",
            description = "iPhone 14 Pro 256GB Deep Purple with clear MagSafe case",
            category = ItemCategory.ELECTRONICS.name,
            imageUrl = "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?auto=format&fit=crop&q=80&w=800",
            proofOfOwnership = "Lockscreen wallpaper is a cat sunset photo, Serial ends in 89F2",
            latitude = 12.9915,
            longitude = 80.2330,
            lostDate = System.currentTimeMillis() - 3600000,
            reportedAt = System.currentTimeMillis() - 3600000 // 1h ago
        ),
        LostItem(
            id = "l2",
            ownerName = "Karthik Raja",
            ownerContact = "karthik.r@campus.edu.in",
            description = "Honda Civic Smart Key Fob with red woven wrist strap",
            category = ItemCategory.KEYS.name,
            imageUrl = "https://images.unsplash.com/photo-1582142407894-ec85a1268a4e?auto=format&fit=crop&q=80&w=800",
            proofOfOwnership = "Scratch on lock button and miniature brass compass keychain",
            latitude = 12.9940,
            longitude = 80.2350,
            lostDate = System.currentTimeMillis() - 10800000,
            reportedAt = System.currentTimeMillis() - 10800000 // 3h ago
        ),
        LostItem(
            id = "l3",
            ownerName = "Rohan Mehta",
            ownerContact = "rohan.m@campus.edu.in",
            description = "The North Face Borealis Navy Blue Backpack",
            category = ItemCategory.BAGS.name,
            imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&q=80&w=800",
            proofOfOwnership = "Contains a grid notebook with name on cover and iPad Air",
            latitude = 12.9885,
            longitude = 80.2320,
            lostDate = System.currentTimeMillis() - 43200000,
            reportedAt = System.currentTimeMillis() - 43200000 // 12h ago
        ),
        LostItem(
            id = "l4",
            ownerName = "Kavya S",
            ownerContact = "kavya.s@campus.edu.in",
            description = "Sony WH-1000XM5 Matte Black Over-Ear Headphones",
            category = ItemCategory.ELECTRONICS.name,
            imageUrl = "https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&q=80&w=800",
            proofOfOwnership = "Custom name 'Kavya's XM5' in Bluetooth device profile",
            latitude = 12.9890,
            longitude = 80.2335,
            lostDate = System.currentTimeMillis() - 129600000,
            reportedAt = System.currentTimeMillis() - 129600000 // 1.5d ago
        ),
        LostItem(
            id = "l5",
            ownerName = "David Chen",
            ownerContact = "david.c@campus.edu.in",
            description = "Texas Instruments TI-84 Plus CE Color Graphing Calculator",
            category = ItemCategory.BOOKS.name,
            imageUrl = "https://images.unsplash.com/photo-1594980596870-8aa52a78d8cd?auto=format&fit=crop&q=80&w=800",
            proofOfOwnership = "Initials 'D.C.' engraved on back cover with silver sharpie",
            latitude = 12.9925,
            longitude = 80.2312,
            lostDate = System.currentTimeMillis() - 216000000,
            reportedAt = System.currentTimeMillis() - 216000000 // 2.5d ago
        ),
        LostItem(
            id = "l6",
            ownerName = "Meera Krishnan",
            ownerContact = "meera.k@campus.edu.in",
            description = "Vintage Blue Denim Trucker Jacket with enamel pins",
            category = ItemCategory.CLOTHING.name,
            imageUrl = "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?auto=format&fit=crop&q=80&w=800",
            proofOfOwnership = "Has NASA pin and space shuttle pin on left collar",
            latitude = 12.9880,
            longitude = 80.2360,
            lostDate = System.currentTimeMillis() - 345600000,
            reportedAt = System.currentTimeMillis() - 345600000 // 4d ago
        )
    )

    override suspend fun addFoundItem(item: FoundItem): Result<String> {
        delay(800)
        val newItem = item.copy(id = "f${foundItems.size + 1}")
        foundItems.add(0, newItem)
        return Result.success(newItem.id)
    }

    override suspend fun addLostItem(item: LostItem): Result<String> {
        delay(800)
        val newItem = item.copy(id = "l${lostItems.size + 1}")
        lostItems.add(0, newItem)
        return Result.success(newItem.id)
    }

    override suspend fun getActiveFoundItems(lastTimestamp: Long?): Result<List<FoundItem>> {
        delay(400)
        return Result.success(foundItems.filter { it.status == "active" && it.expiresAt > System.currentTimeMillis() })
    }

    override suspend fun getActiveLostItems(lastTimestamp: Long?): Result<List<LostItem>> {
        delay(400)
        return Result.success(lostItems.filter { it.status == "active" && it.expiresAt > System.currentTimeMillis() })
    }

    override suspend fun searchFoundItems(filters: SearchFilters): Result<List<FoundItem>> {
        delay(400)
        var result = foundItems.toList()
        if (filters.query.isNotBlank()) {
            result = result.filter { 
                it.description.contains(filters.query, ignoreCase = true) || 
                it.placeFound.contains(filters.query, ignoreCase = true) 
            }
        }
        if (filters.category != null) {
            result = result.filter { it.category == filters.category.name }
        }
        if (filters.location.isNotBlank()) {
            result = result.filter { it.placeFound.contains(filters.location, ignoreCase = true) }
        }
        return Result.success(result)
    }

    override suspend fun searchLostItems(filters: SearchFilters): Result<List<LostItem>> {
        delay(400)
        var result = lostItems.toList()
        if (filters.query.isNotBlank()) {
            result = result.filter { it.description.contains(filters.query, ignoreCase = true) }
        }
        if (filters.category != null) {
            result = result.filter { it.category == filters.category.name }
        }
        return Result.success(result)
    }

    override suspend fun markAsClaimed(itemId: String, isLostItem: Boolean): Result<Unit> {
        delay(400)
        if (isLostItem) {
            val index = lostItems.indexOfFirst { it.id == itemId }
            if (index != -1) lostItems[index] = lostItems[index].copy(status = "claimed", claimed = true)
        } else {
            val index = foundItems.indexOfFirst { it.id == itemId }
            if (index != -1) foundItems[index] = foundItems[index].copy(status = "claimed", claimed = true)
        }
        return Result.success(Unit)
    }

    override suspend fun getFoundItemById(itemId: String): Result<FoundItem> {
        val item = foundItems.find { it.id == itemId }
        return if (item != null) Result.success(item) else Result.failure(Exception("Not found"))
    }

    override suspend fun getLostItemById(itemId: String): Result<LostItem> {
        val item = lostItems.find { it.id == itemId }
        return if (item != null) Result.success(item) else Result.failure(Exception("Not found"))
    }

    override suspend fun getUserFoundItems(userId: String): Result<List<FoundItem>> {
        return Result.success(foundItems.filter { it.reporterId == userId })
    }

    override suspend fun getUserLostItems(userId: String): Result<List<LostItem>> {
        return Result.success(lostItems.filter { it.reporterId == userId })
    }
}
