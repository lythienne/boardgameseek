import React from 'react';
import './App.css';
import './SearchDropdown.js';
import SearchDropdown from './SearchDropdown.js';

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      mapData: {},
      scoresData: [],
      urlsData: [],
      scores: [],
      games: [],
      urls: [],
      searchTerm: ''
    };
  }

  componentDidMount() {
   // Load map.csv
   fetch('map.csv')
     .then(response => response.text())
     .then(text => {
       const mapData = this.parseCsv(text);
       this.setState({ mapData });
     });
 
   // Load scores.txt
   fetch('scores.txt')
     .then(response => response.text())
     .then(text => {
       const scoresData = this.parseScores(text);
       this.setState({ scoresData });
     });

   fetch('urls.txt')
     .then(response => response.text())
     .then(text => {
       const urlsData = this.parseUrls(text);
       this.setState({ urlsData });
     });
 }

  parseCsv = (text) => {
   const lines = text.trim().split('\n');
   const data = {};
   lines.forEach(line => {
     const [game, index] = line.split(',');
     if (game && index) {
       data[game.trim()] = parseInt(index.trim(), 10);
     }
   });
   return data;
 }; 

  parseUrls = (text) => {
   const lines = text.trim().split('\n');
   const data = [];
   let index = 0;
   lines.forEach(line => {
     data[index] = line;
     index++;
   });
   console.log(data);
   return data;
 }; 

 parseScores = (text) => {
   const scoresData = text.split("|").map(function(el){ return el.split(",").map(Number)});
   return scoresData;
 };

  handleSearch = () => {
    const { mapData, scoresData, urlsData, searchTerm } = this.state;
    const index = mapData[searchTerm];
    if (index !== undefined) {
      const row = scoresData[index];
      const scores = [...row].sort((a, b) => b - a).slice(0,5);

      const indexes = [];
      row.forEach((item, index) => {scores.forEach((value) => item === value ? indexes.push(index): null)});
      console.log(indexes);

      const urls = [];
      for (let i = 0; i < 5; i++)
      {
         urls[i] = urlsData[indexes[i]];
      }
      console.log(urls);

      const games = indexes.map(i => Object.keys(mapData).find(key => mapData[key] === i));
      this.setState({ games, scores, urls });
    } 
    else {
      this.setState({ games: [], scores: [], urls: []});
    }
  };

  handleInputChange = (event) => {
    this.setState({ searchTerm: event.target.value });
  };

  render() {
    const { games, scores, urls, searchTerm, mapData } = this.state;
    if(scores.length != 0)
      return (
         <div style={{paddingLeft: 20+'px'}}>
         <h1>Board Game Seek</h1>
         <input type="text" value={searchTerm} onChange={this.handleInputChange} />
         <SearchDropdown>
            
         </SearchDropdown>
         <button onClick={this.handleSearch}>Search</button>
         <h2>Top 5 Most Similar Board Games to {searchTerm}</h2>
         <ul>
               <li key={0}>
                  <p>{games[0]}</p>
                  <p>Similarity: {scores[0].toFixed(3)}</p> 
                  <p>Url: <a href={urls[0]}>{urls[0]}</a></p>
               </li>
               <li key={1}>
                  <p>{games[1]}</p>
                  <p>Similarity: {scores[1].toFixed(3)}</p> 
                  <p>Url: <a href={urls[1]}>{urls[1]}</a></p>
               </li>
               <li key={2}>
                  <p>{games[2]}</p>
                  <p>Similarity: {scores[2].toFixed(3)}</p> 
                  <p>Url: <a href={urls[2]}>{urls[2]}</a></p>
               </li>
               <li key={3}>
                  <p>{games[3]}</p>
                  <p>Similarity: {scores[3].toFixed(3)}</p> 
                  <p>Url: <a href={urls[3]}>{urls[3]}</a></p>
               </li>
               <li key={4}>
                  <p>{games[4]}</p>
                  <p>Similarity: {scores[4].toFixed(3)}</p> 
                  <p>Url: <a href={urls[4]}>{urls[4]}</a></p>
               </li>
         </ul>
         </div>
      );
   else
   return (<div style={{paddingLeft: 20+'px'}}><h1>Board Game Seek</h1>
   <h2>Enter a Board Game Below:</h2>
   <input type="text" value={searchTerm} onChange={this.handleInputChange} />
   <button onClick={this.handleSearch}>Search</button></div>)
  }
}

export default App;
