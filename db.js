
//require means import
const {MongoClient} = require('mongodb');

let dbConnection;



module.exports = {
  connectToDb : (cb)=>{
    MongoClient.connect("mongodb+srv://admin:admin@cluster0.nxlilzi.mongodb.net/test")
    .then((client)=>{
      dbConnection = client.db();
      return cb()
    })
    .catch(err => {
      console.log("OH NO MONGO CONNECTION ERROR!!!");
      console.log(err);
      return cb(err)
    })
  },
  
  getDb : () => dbConnection
}


