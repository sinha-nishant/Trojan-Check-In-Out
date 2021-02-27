// Delete account given user's email
let deleteAccount = async function (client, email) {
    let collection = client.db("TrojanCheck").collection("Accounts");
    const query = { email: email };
    let response = await collection.deleteOne(query);
    if (response.deletedCount === 1) {
        return {
            status: true,
        };
    }
    return {
        status: false,
    };
};

//Create account given user's:
//	firstname,
//	lastname,
//	email,
//	profilePicture
//	password,
//	isManager,
//  uscID,
//  major
let createAccount = async function (
    client,
    firstname,
    lastname,
    email,
    profilePicture,
    password,
    isManager,
    uscID,
    major
) {
    let collection = client.db("TrojanCheck").collection("Accounts");
    const query = {
        firstname: firstname,
        lastname: lastname,
        email: email,
        profilePicture: profilePicture,
        password: password,
        isManager: isManager,
        uscID: uscID,
        major: major,
        activity: [],
    };

    let curr_status = false;
    let response = await collection.insertOne(query).then((result) => {
        curr_status = true;
    });

    if (curr_status) {
        return {
            status: true,
        };
    }
    return {
        status: false,
    };
};

//Check in into a building given user's email, building's name and check in time
let checkIn = async (client, uscID, buildingName, checkInTime) => {
    let collection = client.db("TrojanCheck").collection("Accounts");
    let query = { uscID: uscID };
    const push_query = {
        buildingName: buildingName,
        checkInTime: checkInTime,
        checkOutTime: null,
    };
    
    let curr_status = false;
    console.log("Before status: ", curr_status)

    let response = await collection
        .updateOne(query, { $push: { activity: push_query } })
        .then((result) => {
            curr_status = result.modifiedCount===1;
        });
    
    if(curr_status===false){
        return{
            status:false
        };
    }

    let another_status=false

    collection = client.db("TrojanCheck").collection("Buildings");
    query = {"name": buildingName};
    response = await collection
        .updateOne(query, {$push: {students: uscID}})
        .then((result) => {
            another_status = result.modifiedCount===1;
        });

    console.log("Another status: ", another_status)

    if (curr_status&&another_status) {
        return {
            status: true,
        };
    }
    return {
        status: false,
    };
};

let checkOut = async function(client, uscID, checkOutTime) {
	let collection = client.db("TrojanCheck").collection("Accounts");
	let filter = {
		uscID: uscID
	};

	let update = {
		$pop: {
			activity: 1
		}
	};

	let options = {
		projection: {
			lastActivity: {
				$arrayElemAt: ["$activity", -1]
			}
		},

		returnNewDocument: false
	}

    collection.findOneAndUpdate(filter, update, options, function(err, result) {
		let lastActivity = result.value.lastActivity;
		// console.log(lastActivity);
		update = {
			$push: {
				activity: {
					"buildingName": lastActivity.buildingName,
					"checkInTime": lastActivity.checkInTime,
					"checkOutTime": checkOutTime
				}
			}
		}
		console.log(update);

		collection.updateOne(filter, update, function(err, result) {

			collection = client.db("TrojanCheck").collection("Buildings");

			filter = {
				name: lastActivity.buildingName
			}

			update = {
				$pull: {
					"students" : uscID
				}
			}

			collection.updateOne(filter, update);
		});
	});

	// just for testing purposes
	return {status: true};
};

//exports
module.exports = {
    deleteAccount,
    createAccount,
    checkIn,
	checkOut
};
