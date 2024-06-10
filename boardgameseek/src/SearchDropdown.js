import React, { useEffect, useState } from "react";
import Select from 'react-select';
import AsyncSelect from 'react-select/async';
import makeAnimated from 'react-select/animated';
import './App.css';

const handleChange = (option) => {

   setSelectedOption(option);
   console.log(`Option selected:`, option);
 
 };

const SearchDropdown = (mapData) => {

   const [selectedOption, setSelectedOption] = useState(null);
   
   return (
 
     <div>
      <Select
         className="search-dropdown"
         value={selectedOption}
         onChange={handleChange}
         options={mapData}
      />
     </div>
 
   );
 
 };

 export default SearchDropdown;