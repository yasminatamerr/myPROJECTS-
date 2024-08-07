const cookieParser = require('cookie-parser');
var express = require('express');
var server = express();
var path = require('path');
var bodyParser = require('body-parser');
var sessions = require('express-session');
const MemoryStore = require('memorystore')(sessions)


server.set('views',path.join(__dirname, 'views'));
server.set('view engine','ejs');



server.use(express.json());
server.use(express.urlencoded({extended: false}));
server.use(express.static(path.join(__dirname, 'public')));
server.use(bodyParser.json());
server.use(cookieParser());



var session 

server.use(sessions({
  secret : "6436A1449Y16408N2168J18094E-JAENY-May-FEB-DEC-OCT",
  saveUninitialized:true,
  cookie : {maxAge : 1000*60*60*60*5},
  store: new MemoryStore({
    checkPeriod: 86400000 // prune expired entries every 24h
  }),
  resave : false
}));



  
const {connectToDb , getDb} = require('./db');

let db

connectToDb((err)=> {
  if(!err){
    server.listen(3000, ()=>{
      console.log("SERVER IS LISTENING ON PORT 3000")
    })
    db = getDb()
  }
  else{
    console.log(err);
  }

})

///////////////////////////////////////////////////////////////////////////////////////////


server.get('/', (req,res) => {
  res.render('login');
})

server.get('/login', (req,res) => {
  res.render('login');
})

//Log in
server.post('/login', async (req,res) =>{

  var name = req.body.username; //retrieve the username value from the front end
  var pass = req.body.password;//retrieve the password value from the front end
  
  //find an object that it's username attribute has the value name and password has value pass
  //const user (will hold an object if it was found in the database otherwise it will hold Null)
  const user = await db.collection('users').findOne({username : name , password : pass});

  //if const user was found in the database it will hold an object otherwise will hold a NULL
  //we need to check if that user is not in the database which means that he/she does not have an account
  if(user === null){
    var msg = "You are not signed up, Please create an account!";
    res.render('message',{msg});
    return;
  }
  
  //the server needs to serve each user separately so each one will have it's own session 
  session = req.session 
  session.userid = req.body.username // the session id is the username of the user because it's unique for each user
  
  await db.collection('loggedin').insertOne({username:name})

  res.render('home');

})




server.get('/registration', (req,res) =>{
  res.render('registration');
  //remember that we need to create a get method for all webpages
});





server.post('/register', async (req,res) =>{
  var name = req.body.username;//retrieve the username value from the front end
  var pass = req.body.password;//retrieve the password value from the front end
  //find an object that it's username attribute has the value name that is retrieved from the front end
  //const user (will hold an object if it was found in the database otherwise it will hold Null)
  var user = await db.collection('users').findOne( {username : name});
  //this is to check if the user was found already in the database in that case display an error message
  if(user != null){
    var msg  = "Username already token please choose another one";
    res.render('message',{msg}); // render to the message page with a variable 'content' to be displayed as an HTML text between <h1> </h1>
    return ; // return in order not to execute the rest of the method because we don't want to add to the DB in that case
  }
  //to check if one of the input fields is empty
  if(name == "" || pass == ""){
    var msg  = "Username or Password field is missing";
    res.render('message',{msg});
    return ; // return in order not to execute the rest of the method because we don't want to add to the DB in that case
  }
  //create the user object in json notation
  //attribute username takes the value name which is retrieved from the front end
  //attribute password takes the value pass which is retrieved from the front end as well
  var user = {username : name , password : pass};
  //add it to the database (which means that someone created an account)
  //hence they can login and use the website 
  await db.collection('users').insertOne(user);
  //send that user to the login page after registeration is successful(as required in the description)
  //we use redirect to go one page back while render to go one page forward
  res.redirect('/login');

})
//////////////////////////////////////////////////////////////////////////////////////

server.get('/home', async (req,res) =>{
  var user = await db.collection('loggedin').findOne({username:req.session.userid});

  if(user === null){
    var msg = "You are not logged in"
    res.render('message',{msg})
    return;
  }

  res.render('home');
});





server.get('/hiking',async (req,res)=>{
  var user = await db.collection('loggedin').findOne({username:req.session.userid});

  if(user === null){
    var msg = "You are not logged in"
    res.render('message',{msg})
    return;
  }

  res.render('hiking')
})






server.get('/cities',async (req,res) =>{
  var user = await db.collection('loggedin').findOne({username:req.session.userid});

  if(user === null){
    var msg = "You are not logged in"
    res.render('message',{msg})
    return;
  }
  res.render('cities')
})





server.get('/islands',async (req,res) =>{  
  var user = await db.collection('loggedin').findOne({username:req.session.userid});

  if(user === null){
    var msg = "You are not logged in"
    res.render('message',{msg})
    return;
  }
  res.render('islands')    

})
    




server.get('/paris',async (req,res) =>{
  var user = await db.collection('loggedin').findOne({username:req.session.userid});

  if(user === null){
    var msg = "You are not logged in"
    res.render('message',{msg})
    return;
  }
  res.render('paris')
})





server.get('/rome',async (req,res) =>{
  var user = await db.collection('loggedin').findOne({username:req.session.userid});

  if(user === null){
    var msg = "You are not logged in"
    res.render('message',{msg})
    return;
  }

  res.render('rome')
})




server.get('/santorini',async (req,res) =>{
  var user = await db.collection('loggedin').findOne({username:req.session.userid});

  if(user === null){
    var msg = "You are not logged in"
    res.render('message',{msg})
    return;
  }
  res.render('santorini');

})




server.get('/bali',async (req,res) =>{
  var user = await db.collection('loggedin').findOne({username:req.session.userid});

  if(user === null){
    var msg = "You are not logged in"
    res.render('message',{msg})
    return;
  }
  res.render('bali')

})




server.get('/annapurna',async (req,res) =>{
  var user = await db.collection('loggedin').findOne({username:req.session.userid});

  if(user === null){
    var msg = "You are not logged in"
    res.render('message',{msg})
    return;
  }
  res.render('annapurna')

})




server.get('/inca',async (req,res) =>{
  var user = await db.collection('loggedin').findOne({username:req.session.userid});

  if(user === null){
    var msg = "You are not logged in"
    res.render('message',{msg})
    return;
  }
  res.render('inca')

})





server.post('/add', async (req,res) =>{
  var name = session.userid;
  var dest = req.body.destination;

  var destinationObject = await db.collection('userDestination').findOne({username:name, destination:dest});

  if(destinationObject != null){
    var msg = "Destination exists already!";
    res.render('message',{msg});
    return;
  }

  await db.collection('userDestination').insertOne({username:name,destination:dest});
  res.redirect('/home');
});





server.get('/wanttogo',async (req,res) => {
  var userList = await db.collection('userDestination').find({username : req.session.userid}).toArray();
  
  
  res.render('wanttogo',{userList} );

})




server.post('/remove', async (req,res) =>{
  await db.collection('userDestination').deleteOne({username : req.session.userid , destination : req.body.dest})
  
  var userList = await db.collection('userDestination').find({username : req.session.userid}).toArray();
  res.render('wanttogo',{userList});
})





server.post('/search', async (req,res)=> {
  var str = req.body.Search;
  var list = await db.collection('destinations').find({destination: {'$regex' : `${str}`, '$options' : 'i'} }).toArray()
  

  if(list.length == 0 || str.length === 0 || str == "."){
    var msg = "Item not found";
    res.render('message',{msg});
    return;
  }

  res.render('searchresults',{list})

})





server.get('/logout', async (req,res)=>{

  await db.collection('loggedin').deleteMany({username:req.session.userid})

  req.session.destroy();
  res.render('login');

})

// var dest = ["annapurna","bali","inca","paris","rome","santorini"]

//   for(let i=0 ; i < dest.length ; i++){
//     var list = await db.collection('destinations').insertOne({destination:dest[i]})
//   }
