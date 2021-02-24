const typeDef = `
	type Building {
		name: String, 
		capacity: Int,
		occupancy: Int,
		students: [Int]
	}
`;

let getBuilding = async function (client, name) {
    const collection = client.db("TrojanCheck").collection("Buildings");
    let query = {name: name};
    let building = await collection.findOne(query);
    return [building];
};

let getAllBuildings = async function(client) {
    const collection = client.db("TrojanCheck").collection("Buildings");
    let buildings = await collection.find().toArray();
    return buildings;
}

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