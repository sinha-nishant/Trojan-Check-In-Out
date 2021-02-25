const typeDef = `
	type Building {
		name: String, 
		capacity: Int,
		occupancy: Int,
		students: [Int]
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

module.exports = {
	typeDef,
	getBuilding,
	getAllBuildings,
	updateCapacity
}