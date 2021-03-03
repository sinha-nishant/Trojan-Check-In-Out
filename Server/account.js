const ActivityTypeDef = `
	type Activity {
		buildingName: String,
        checkInTime: String,
        checkOutTime: String
	}
`;

const StudentTypeDef = `
	type Student {
		firstName: String, 
        lastName: String,
        email: String,
        profilePicture: String,
        password: String,
        isManager: Boolean,
        uscID: Int,
        major: String,
		activity: [Activity]
	}
`;

// Delete account given user's email
async function deleteAccount(client, email) {
    let response = null;
    try {
        let collection = client.db("TrojanCheck").collection("Accounts");
        const query = { email: email };
        response = await collection.deleteOne(query);

        if (response.deletedCount === 1) {
            return {
                status: true,
            };
        }
    } catch (error) {
        console.error(error);
        return {
            status: false,
        };
    }

    return {
        status: false,
    };
}

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
    console.log("Before status: ", curr_status);

    let response = await collection
        .updateOne(query, { $push: { activity: push_query } })
        .then((result) => {
            curr_status = result.modifiedCount === 1;
        });

    if (curr_status === false) {
        return {
            status: false,
        };
    }

    let another_status = false;

    collection = client.db("TrojanCheck").collection("Buildings");
    query = { name: buildingName };
    response = await collection
        .updateOne(query, { $push: { students: uscID } })
        .then((result) => {
            another_status = result.modifiedCount === 1;
        });

    console.log("Another status: ", another_status);

    if (curr_status && another_status) {
        return {
            status: true,
        };
    }
    return {
        status: false,
    };
};

// TODO: Add exception handling
async function checkOut(client, uscID, checkOutTime) {
    try {
        let collection = client.db("TrojanCheck").collection("Accounts");
        let filter = {
            uscID: uscID,
        };

        let update = {
            $pop: {
                activity: 1,
            },
        };

        let options = {
            projection: {
                lastActivity: {
                    $arrayElemAt: ["$activity", -1],
                },
            },

            returnOriginal: true,
        };

        // Pops last activity node
        collection.findOneAndUpdate(
            filter,
            update,
            options,
            function (err, result) {
                let lastActivity = result.value.lastActivity;

                update = {
                    $push: {
                        activity: {
                            buildingName: lastActivity.buildingName,
                            checkInTime: lastActivity.checkInTime,
                            checkOutTime: checkOutTime,
                        },
                    },
                };

                if (result.lastErrorObject.updatedExisting !== true) {
                    return { status: false };
                }

                // Inserts new activity node based on popped with new check-out time
                collection.updateOne(filter, update, function (err, result) {
                    if (result.modifiedCount !== 1) {
                        return { status: false };
                    }

                    collection = client
                        .db("TrojanCheck")
                        .collection("Buildings");

                    filter = {
                        name: lastActivity.buildingName,
                    };

                    update = {
                        $pull: {
                            students: uscID,
                        },

                        $inc: {
                            occupancy: -1,
                        },
                    };

                    // Deletes student ID from building's current student array
                    collection.updateOne(filter, update, (err, result) => {
                        if (result.modifiedCount !== 1) {
                            return { status: false };
                        }
                    });
                });
            }
        );
    } catch (error) {
        console.error(error);
        return { status: false };
    }

    return { status: true };
}

// Retrieve a student's profile based on their USC ID
async function getStudent(client, uscID) {
    const collection = client.db("TrojanCheck").collection("Accounts");
    const query = { uscID: uscID };
    let result = null;
    try {
        result = await collection.findOne(query);
    } catch (error) {
        console.error(error);
        return null;
    }

    return result;
}

//exports
module.exports = {
    ActivityTypeDef,
    StudentTypeDef,
    deleteAccount,
    createAccount,
    checkIn,
    checkOut,
    getStudent,
};
