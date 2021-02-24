// INCOMPLETE, but current code is operational
let deleteAccount = async function (client, email) {
	let collection = client.db("TrojanCheck").collection("Accounts");
	const query = {"email": email};
	collection.remove(query);
    return {
		status: true
	};
};

module.exports = {
	deleteAccount
}