// Delete account given user's email
let deleteAccount = async function (client, email) {
	let collection = client.db("TrojanCheck").collection("Accounts");
	const query = {"email": email};
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
	major) 
	{
	let collection = client.db("TrojanCheck").collection("Accounts");
	const query = {"firstname": firstname,
	"lastname": lastname,
	"email": email,
	"profilePicture": profilePicture,
	"password": password,
	"isManager": isManager,
	"uscID": uscID,
	"major": major,
	"activity": []};

	let curr_status=false
	let response = await collection.insertOne(query).then(result => {curr_status=true});
	
	if(curr_status){
		return {
			status: true
		};
	}
	return {
		status: false
	};

}

//Check in into a building given user's email, building's name and check in time
let checkIn = async (client, email, buildingName, checkInTime)=>{
	let collection = client.db("TrojanCheck").collection("Accounts");
	const query = {"email": email}
	const push_query = {"buildingName": buildingName, "checkInTime": checkInTime, "checkOutTime":0}
	let curr_status=false

	let response = await collection.updateOne(query, {$push: {"activity": push_query}}).then(result => {curr_status=true});

	if(curr_status){
		return {
			status: true
		};
	}
	return {
		status: false
	};
}


//exports
module.exports = {
	deleteAccount,
	createAccount,
	checkIn
}

