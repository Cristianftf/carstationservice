import React, { useState, useEffect } from 'react';
import axios from 'axios';
import StationList from './components/StationList';
import StationForm from './components/StationForm';
import SearchStations from './components/SearchStations';
import Statistics from './components/Statistics';
import Login from './components/Login';
import Register from './components/Register';
import { setupAxiosInterceptors, isAuthenticated, logout, getCurrentUserEmail } from './utils/auth';
import './App.css';

function App() {
  const [stations, setStations] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('list');
  const [editingStation, setEditingStation] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [isLoggedIn, setIsLoggedIn] = useState(isAuthenticated());
  const [showLogin, setShowLogin] = useState(true);
  const [userEmail, setUserEmail] = useState(getCurrentUserEmail());

  useEffect(() => {
    setupAxiosInterceptors();
    if (isLoggedIn) {
      fetchStations();
    }
  }, [refreshTrigger, isLoggedIn]);

  const handleLogin = (token, email) => {
    setIsLoggedIn(true);
    setUserEmail(email);
    setShowLogin(true);
  };

  const handleRegister = () => {
    setShowLogin(true);
    alert('Registro exitoso. Por favor inicia sesión.');
  };

  const handleLogout = () => {
    logout();
    setIsLoggedIn(false);
    setUserEmail(null);
  };

  const switchToRegister = () => {
    setShowLogin(false);
  };

  const switchToLogin = () => {
    setShowLogin(true);
  };

  const fetchStations = async () => {
    try {
      setLoading(true);
      const response = await axios.get('/api/charging-stations');
      setStations(response.data);
      setError(null);
    } catch (err) {
      setError('Error al cargar las estaciones: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleCreateStation = async (stationData) => {
    try {
      await axios.post('/api/charging-stations', stationData);
      setRefreshTrigger(prev => prev + 1);
      setActiveTab('list');
      return { success: true, message: 'Estación creada exitosamente' };
    } catch (err) {
      return { success: false, message: 'Error al crear estación: ' + (err.response?.data?.message || err.message) };
    }
  };

  const handleUpdateStation = async (stationData) => {
    try {
      await axios.put(`/api/charging-stations/${stationData.id}`, stationData);
      setRefreshTrigger(prev => prev + 1);
      setEditingStation(null);
      setActiveTab('list');
      return { success: true, message: 'Estación actualizada exitosamente' };
    } catch (err) {
      return { success: false, message: 'Error al actualizar estación: ' + (err.response?.data?.message || err.message) };
    }
  };

  const handleDeleteStation = async (id) => {
    try {
      await axios.delete(`/api/charging-stations/${id}`);
      setRefreshTrigger(prev => prev + 1);
      return { success: true, message: 'Estación eliminada exitosamente' };
    } catch (err) {
      return { success: false, message: 'Error al eliminar estación: ' + (err.response?.data?.message || err.message) };
    }
  };

  const handleEditStation = (station) => {
    setEditingStation(station);
    setActiveTab('form');
  };

  const handleCancelEdit = () => {
    setEditingStation(null);
    setActiveTab('list');
  };

  const handleSearchStations = async (searchParams) => {
    try {
      setError(null);
      const response = await axios.get('/api/charging-stations/search', {
        params: searchParams
      });
      setSearchResults(response.data);
      setActiveTab('list');
    } catch (err) {
      setError('Error en la búsqueda: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleClearSearch = () => {
    setSearchResults([]);
    setIsSearching(false);
    setRefreshTrigger(prev => prev + 1);
  };

  // Show authentication forms if not logged in
  if (!isLoggedIn) {
    return (
      <div className="App">
        <header className="header">
          <h1>Sistema de Gestión de Estaciones de Carga</h1>
          <p>Inicia sesión para administrar las estaciones de carga</p>
        </header>
        
        <div className="container">
          {showLogin ? (
            <Login onLogin={handleLogin} onSwitchToRegister={switchToRegister} />
          ) : (
            <Register onRegister={handleRegister} onSwitchToLogin={switchToLogin} />
          )}
        </div>
      </div>
    );
  }

  // Show main application if logged in
  return (
    <div className="App">
      <header className="header">
        <div className="header-content">
          <div>
            <h1>Sistema de Gestión de Estaciones de Carga</h1>
            <p>Administra y monitorea las estaciones de carga para vehículos eléctricos</p>
          </div>
          <div className="user-info">
            <span>Bienvenido, {userEmail}</span>
            <button onClick={handleLogout} className="btn btn-secondary">
              Cerrar Sesión
            </button>
          </div>
        </div>
      </header>

      <div className="container">
        <nav className="tabs">
          <button 
            className={activeTab === 'list' ? 'btn btn-primary' : 'btn btn-secondary'} 
            onClick={() => setActiveTab('list')}
          >
            Lista de Estaciones
          </button>
          <button 
            className={activeTab === 'form' ? 'btn btn-primary' : 'btn btn-secondary'} 
            onClick={() => setActiveTab('form')}
          >
            {editingStation ? 'Editar Estación' : 'Nueva Estación'}
          </button>
          <button 
            className={activeTab === 'search' ? 'btn btn-primary' : 'btn btn-secondary'} 
            onClick={() => setActiveTab('search')}
          >
            Buscar Estaciones
          </button>
          <button 
            className={activeTab === 'stats' ? 'btn btn-primary' : 'btn btn-secondary'} 
            onClick={() => setActiveTab('stats')}
          >
            Estadísticas
          </button>
        </nav>

        {activeTab === 'list' && (
          <>
            {searchResults.length > 0 && (
              <div className="search-results-header">
                <h3>Resultados de Búsqueda ({searchResults.length})</h3>
                <button
                  className="btn btn-secondary"
                  onClick={handleClearSearch}
                >
                  Limpiar Búsqueda
                </button>
              </div>
            )}
            <StationList
              stations={searchResults.length > 0 ? searchResults : stations}
              onEdit={handleEditStation}
              onDelete={handleDeleteStation}
            />
          </>
        )}

        {activeTab === 'form' && (
          <StationForm
            station={editingStation}
            onSave={editingStation ? handleUpdateStation : handleCreateStation}
            onCancel={editingStation ? handleCancelEdit : () => setActiveTab('list')}
          />
        )}

        {activeTab === 'search' && (
          <SearchStations onSearch={handleSearchStations} />
        )}

        {activeTab === 'stats' && (
          <Statistics />
        )}
      </div>
    </div>
  );
}

export default App;
