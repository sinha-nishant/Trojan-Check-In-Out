// To retrieve environment variables for MongoDB auth
require("dotenv").config();

// Package Imports
const express = require("express");
const { ApolloServer, gql } = require('apollo-server-express');

// Instantiate MongoDB Connection
const MongoClient = require("mongodb").MongoClient;
const console = require("console");
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

// Query defines all of the top-level entry points for queries
const Query = gql `
	type Query {
		building(name: String): [Building]
	}
`;

const Mutation = gql `
    type Mutation {
        updateCapacity(name: String!, capacity: Int!): Status
        deleteAccount(email: String!): Status
    }
`;

const Building = require("./building");
const Account = require("./account");
const Status = require("./status");
const typeDefs = [Query, Mutation, Building.typeDef, Status.typeDef];

// The resolvers
const resolvers = {
    Query: {
        building(_, args) {
            if (args.hasOwnProperty("name")) {
                return Building.getBuilding(client, args.name);
            }
            return Building.getAllBuildings(client);
        },
    },
    Mutation: {
        updateCapacity(_, args) {
            return Building.updateCapacity(client, args.name, args.capacity);
        },
        deleteAccount(_, args) {
            return Account.deleteAccount(client, args.email);
        }
    },
};

// Initialize the app
const app = express();

const PORT = 4000;

const server = new ApolloServer({ typeDefs, resolvers });
server.applyMiddleware({ app });

app.listen({ port: PORT }, () =>
  console.log(`🚀 Server ready at http://localhost:4000${server.graphqlPath}`)
)