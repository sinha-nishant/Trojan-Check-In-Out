const BuildingTypeDef = `
	type Building {
		name: String, 
		capacity: Int,
		occupancy: Int,
		students: [Int]
	}
`;

// Get building given its name
async function getBuilding(client, name) {
    let building = null;
    try {
        const collection = client.db("TrojanCheck").collection("Buildings");
        let query = { name: name };
        building = await collection.findOne(query);
    } catch (error) {
        console.error(error);
        return null;
    }

    return [building];
}

// Get all buildings
async function getAllBuildings(client) {
    let buildings = null;
    try {
        const collection = client.db("TrojanCheck").collection("Buildings");
        buildings = await collection.find().toArray();
    } catch (error) {
        console.error(error);
        return null;
    }

    return buildings;
}

// Update the capacity of a building given the name of the building and its new capacity
async function updateCapacity(client, name, capacity) {
    try {
        let collection = client.db("TrojanCheck").collection("Buildings");
        let filter = {
            name: name,
        };
        let update = {
            $set: {
                capacity: capacity,
            },
        };

        let response = await collection.updateOne(filter, update);
        if (response.modifiedCount !== 1) {
            return {
                status: false,
            };
        }
    } catch (error) {
        console.error(error);
        return {
            status: false,
        };
    }

    return {
        status: true,
    };
}

// Batch update building capacities
async function updateCapacities(client, buildingNames, newCapacities) {
    try {
        let collection = client.db("TrojanCheck").collection("Buildings");
        let updates = [];
        for (let i = 0; i < buildingNames.length; i++) {
            updates.push({
                updateOne: {
                    filter: { name: buildingNames[i] },
                    update: { $set: { capacity: newCapacities[i] } },
                },
            });
        }

        // "Ordered: false" indicates updates don't have to be made in order
        // Increases performance
        let response = await collection.bulkWrite(updates, { ordered: false });
        if (response.modifiedCount !== buildingNames.length) {
            return { status: false };
        }
    } catch (error) {
        console.error(error);
        return { status: false };
    }

    return { status: true };
}

module.exports = {
    BuildingTypeDef,
    getBuilding,
    getAllBuildings,
    updateCapacity,
    updateCapacities,
};
