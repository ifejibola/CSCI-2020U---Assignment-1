// TODO: onload function should retrieve the data needed to populate the UI

async function getSpamResults(url)
{
  await fetch(url,{
    method: 'GET',
    headers:{
      'Accept' : 'application/json'
    }
  }).then(response => response.json())
    .then(response => {

      console.log('spam: ',response);

      // New Td's
      // const newFileTd = document.createElement("td");
      // const newProbabilityTd = document.createElement("td");
      // const newClassTd = document.createElement("td");
      // const concat = document.createElement("tr");

      // Inputs
      // let fileContent;
      // let probilityContent;
      // let classContent;
      // for(let i = 0; i < response.length; i++)
      for(let i = 0; i < response.length; i++)
      {
        // console.log(response[i]);
        // console.log(response[i].file);
        //
        // console.log(response[i].spamProbability);

        const newFileTd = document.createElement("td");
        const newProbabilityTd = document.createElement("td");
        const newClassTd = document.createElement("td");
        const concat = document.createElement("tr");

        let fileContent;
        let probilityContent;
        let classContent;

        fileContent = document.createTextNode(response[i].file);
        probilityContent = document.createTextNode(response[i].spamProbability);
        classContent = document.createTextNode(response[i].actualClass);

        newFileTd.appendChild(fileContent);
        newProbabilityTd.appendChild(probilityContent);
        newClassTd.appendChild(classContent);

        // Concatnate all td's
        concat.append(newFileTd);
        concat.append(newProbabilityTd);
        concat.append(newClassTd);

        // console.log(concat.innerHTML);

        // add to table
        document.getElementById("tbody").innerHTML += concat.innerHTML;
      }

  });
}


async function getPrecision(url)
{
 await fetch(url, {
    method: 'GET',
    headers: {
      'Accept': 'application/json'
    }
  }).then(response  => response.json())
    .then(response => {
      console.log('\ngetPrecision: ', response);

      const precesionLabel = document.getElementById("precesionLabel");
      // const value = document.createTextNode(response);

      precesionLabel.innerHTML = JSON.stringify(response);

    });
}


async function getAcc(url)
{
  await fetch(url, {
    method: 'GET',
    headers: {
      'Accept': 'application/json'
    }
  }).then(response  => response.json())
    .then(response => {
      console.log('\ngetPrecision: ', response);

      const accLabel = document.getElementById("accLabel");
      // const value = document.createTextNode(response);

      accLabel.innerHTML = JSON.stringify(response);

    });
}

let apiUrl  = "http://localhost:8080/spamDetector-1.0/api/spam";
let precisionApi = "http://localhost:8080/spamDetector-1.0/api/spam/precision";
let accApi = "http://localhost:8080/spamDetector-1.0/api/spam/accuracy";

(function (){
  getSpamResults(apiUrl);
  getPrecision(precisionApi);
  getAcc(accApi)
})();

//



