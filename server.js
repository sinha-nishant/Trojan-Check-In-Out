// To retrieve environment variables for MongoDB auth
require("dotenv").config();

// Package Imports
const mongo = require("mongodb");
const express = require("express");
const bodyParser = require("body-parser");
const { graphqlExpress, graphiqlExpress } = require("apollo-server-express");
const { makeExecutableSchema } = require("graphql-tools");

// Instantiate MongoDB Connection
const MongoClient = require("mongodb").MongoClient;
const console = require("console");
const { argsToArgsConfig } = require("graphql/type/definition");
const uri = `mongodb+srv://${process.env.USERNAME}:${process.env.PASSWORD}@tcio.yhcmw.mongodb.net/TrojanCheck?retryWrites=true&w=majority`;
const client = new MongoClient(uri, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
});

let connectClient = async function () {
    try {
        // Cannot proceed until connection is formed
        await client.connect();
    } catch (error) {
        console.error(error);
    }
};

connectClient();

let getBuilding = function (name) {
    const collection = client.db("TrojanCheck").collection("Buildings");
    let query = { name: name };
    let building = collection.findOne(query);
    return building;
};

// Query defines all of the top-level entry points for queries
const Query = `
	type Query {
		building(name: String!) : Building
	}
`;

const Building = require("./building");
const typeDefs = [
	Query, 
	Building.typeDef
];

// The resolvers
const resolvers = {
    Query: {
        building(_, args) {
            return getBuilding(args.name);
        },
    },
};

// Put together a schema
const schema = makeExecutableSchema({
    typeDefs,
    resolvers,
});

// Initialize the app
const app = express();

// The GraphQL endpoint
app.use("/graphql", bodyParser.json(), graphqlExpress({ schema }));

// GraphiQL, a visual editor for queries
app.use("/graphiql", graphiqlExpress({ endpointURL: "/graphql" }));

// Start the server
app.listen(3000, () => {
    console.log("Go to http://localhost:3000/graphiql to run queries!");
});
