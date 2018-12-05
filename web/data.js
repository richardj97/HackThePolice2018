
firebase.auth().onAuthStateChanged(function(user) {
  if (user) {
    // User is signed in.

    document.getElementById("user_div").style.display = "block";
    document.getElementById("login_div").style.display = "none";

    var user = firebase.auth().currentUser;

    if(user != null){

      var email_id = user.email;
      document.getElementById("user_para").innerHTML = "Welcome User : " + email_id;
    }

  } else {
    // No user is signed in.
    document.getElementById("user_div").style.display = "none";
    document.getElementById("login_div").style.display = "block";
  }
});

function logout(){
 try {
  firebase.auth().signOut();
    window.location.href = "index.html";
 } catch (e){
    window.alert("An error occurred while signing out");
 }
}

function GetAllCrimes(){
 var db = firebase.firestore();
 db.settings({
  timestampsInSnapshots: true
 });
 
 var collectionReference = db.collection('crimes').get().then(function(querySnapshot) {
  if (querySnapshot.empty) {
  console.log('no documents found');
 } else {
  // go through all the results
 querySnapshot.forEach(function (documentSnapshot) {
  var data = documentSnapshot.data();
 
var table = document.getElementById("crimes");

var row = table.insertRow(1);

var cell1 = row.insertCell(0);
var cell2 = row.insertCell(1);
var cell3 = row.insertCell(2);
var cell4 = row.insertCell(3);

cell1.innerHTML = data.crimetype;
cell2.innerHTML = data.vehreg;
cell3.innerHTML = data.locationlon;
cell4.innerHTML = data.locationlat;
  
});
}
});
}

function QueryBy(data, value){
  var db = firebase.firestore();
 db.settings({
  timestampsInSnapshots: true
 });
var collectionReference = db.collection('Crimes');
 var query = collectionReference.where(data, '==', value);
 query.get().then(function(querySnapshot) {
  if (querySnapshot.empty) {
  console.log('no documents found');
} else {
  // go through all the results
querySnapshot.forEach(function (documentSnapshot) {
  var data = documentSnapshot.data();
  console.log(data.VehReg);
  console.log(data.LocationLat);
  console.log(data.LocationLon);
});
}
});
}


function addData(){
 window.alert("Data added to ddatabase!");
     window.location.href = "data.html";
}


function openForm() {
    document.getElementById("myForm").style.display = "block";
}

function closeForm() {
    document.getElementById("myForm").style.display = "none";
}