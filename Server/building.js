const BuildingTypeDef = `
	type Building {
		name: String, 
		capacity: Int,
		occupancy: Int,
		students: [Int]
	}
`;

const CapacityUpdateTypeDef = `
	type CapacityUpdate {
		name: String, 
		newCapacity: Int
	}
`;

// Get building given its name
let getBuilding = async function (client, name) {
    const collection = client.db("TrojanCheck").collection("Buildings");
    let query = {name: name};
    let building = await collection.findOne(query);
    return [building];
};

// Get all buildings
let getAllBuildings = async function(client) {
    const collection = client.db("TrojanCheck").collection("Buildings");
    let buildings = await collection.find().toArray();
    return buildings;
}

// Update the capacity of a building given the name of the building and its new capacity
let updateCapacity = async function (client, name, capacity) {
    let collection = client.db("TrojanCheck").collection("Buildings");
    let filter = {
        name: name,
    };
    let update = {
        $set: {
            capacity: capacity,
        },
    };

    try {
        let response = await collection.updateOne(filter, update);
        if (response.modifiedCount === 1) {
            return {
                status: true,
            };
        }
        return {
            status: false,
        };
    } catch (err) {
        console.error(`Something went wrong: ${err}`);
        return {
            status: false,
        };
    }
};

let updateCapacities = function(client, buildingNames, newCapacities) {
    let collection = client.db("TrojanCheck").collection("Buildings");
    let updates = []
    for (let i = 0; i < buildingNames.length; i++) {
        updates.push(
            {updateOne: 
                {
                    filter: {name: buildingNames[i]},
                    update: {$set: {capacity: newCapacities[i]}}
                }
            }
        );
    }

    collection.bulkWrite(updates);

    // for testing purposes
    return {status: true};
};

module.exports = {
	BuildingTypeDef,
    CapacityUpdateTypeDef,
	getBuilding,
	getAllBuildings,
	updateCapacity,
    updateCapacities
}