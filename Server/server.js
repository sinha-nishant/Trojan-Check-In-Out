// To retrieve environment variables for MongoDB auth
require("dotenv").config();

// Package Imports
const { ApolloServer, gql } = require("apollo-server");

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
const Query = gql`
    type Query {
        building(name: String): [Building]
    }
`;

const Mutation = gql`
    type Mutation {
        updateCapacity(name: String!, capacity: Int!): Status
        deleteAccount(email: String!): Status
        createAccount(
            firstname: String!
            lastname: String!
            email: String!
            password: String!
            profilePicture: String!
            uscID: Int!
            major: String!
            isManager: Boolean!
        ): Status
        checkIn(
            buildingName: String!
            uscID: Int!
            checkInTime: String!
        ): Status
        checkOut(
            uscID: Int!
            checkOutTime: String!
        ) : Status
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
        },

        createAccount(_, args) {
            return Account.createAccount(
                client,
                args.firstname,
                args.lastname,
                args.email,
                args.profilePicture,
                args.password,
                args.isManager,
                args.uscID,
                args.major
            );
        },

        checkIn(_, args) {
            return Account.checkIn(
                client,
                args.uscID,
                args.buildingName,
                args.checkInTime
            );
        },
        checkOut(_, args) {
            return Account.checkOut(
                client,
                args.uscID,
                args.checkOutTime
            )
        }
    },
};

const server = new ApolloServer({ typeDefs, resolvers });

server.listen({ port: process.env.PORT || 4000 }).then(({ url }) => {
    console.log(`🚀 Server ready at ${url}`);
});
