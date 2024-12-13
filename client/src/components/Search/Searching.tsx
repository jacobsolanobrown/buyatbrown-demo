import React, { useState } from 'react';
import { filterListings } from '../../utils/api';
import { SearchBar } from './SearchBar';
import { useNavigate } from 'react-router-dom';


const Searching = () => {
    const [loading, setLoading] = useState(false);
    const [results, setResults] = useState([]);
    const navigate = useNavigate();

    const handleSearchSubmit = (term: string) => {
        setLoading(true);
        filterListings(term, "ignore", "ignore", "ignore")
            .then((data) => {
                setResults(data.filtered_listings || []);
                navigate('/search-results', { state: { searchTerm: term, filteredPosts: data.filtered_listings || []} });
            })
            .catch(console.error)
            .finally(() => setLoading(false));
    };

    return (
        <>
            <SearchBar onSearchSubmit={handleSearchSubmit} />
            {loading && <p>Searching...</p>}
        </>
    );
};
