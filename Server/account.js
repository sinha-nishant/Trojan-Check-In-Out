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

module.exports = {
	deleteAccount
}