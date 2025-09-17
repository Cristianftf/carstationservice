import React, { useState } from 'react';

const SearchStations = ({ onSearch }) => {
  const [searchData, setSearchData] = useState({
    latitude: '',
    longitude: '',
    radius: '5'
  });

  const [errors, setErrors] = useState({});

  const validateForm = () => {
    const newErrors = {};

    if (!searchData.latitude) {
      newErrors.latitude = 'La latitud es requerida';
    } else if (isNaN(searchData.latitude) || searchData.latitude < -90 || searchData.latitude > 90) {
      newErrors.latitude = 'La latitud debe ser un número entre -90 y 90';
    }

    if (!searchData.longitude) {
      newErrors.longitude = 'La longitud es requerida';
    } else if (isNaN(searchData.longitude) || searchData.longitude < -180 || searchData.longitude > 180) {
      newErrors.longitude = 'La longitud debe ser un número entre -180 y 180';
    }

    if (!searchData.radius || searchData.radius < 1 || searchData.radius > 100) {
      newErrors.radius = 'El radio debe ser un número entre 1 y 100 km';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) {
      const searchParams = {
        latitude: parseFloat(searchData.latitude),
        longitude: parseFloat(searchData.longitude),
        radius: parseFloat(searchData.radius)
      };
      onSearch(searchParams);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setSearchData(prev => ({
      ...prev,
      [name]: value
    }));

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const handleUseCurrentLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setSearchData(prev => ({
            ...prev,
            latitude: position.coords.latitude.toFixed(6),
            longitude: position.coords.longitude.toFixed(6)
          }));
        },
        (error) => {
          alert('No se pudo obtener la ubicación actual. Por favor, ingresa las coordenadas manualmente.');
          console.error('Error getting location:', error);
        }
      );
    } else {
      alert('La geolocalización no es soportada por este navegador.');
    }
  };

  return (
    <div className="card">
      <h3>Buscar Estaciones Cercanas</h3>
      
      <form onSubmit={handleSubmit} className="form">
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="latitude">Latitud:</label>
            <input
              type="number"
              id="latitude"
              name="latitude"
              value={searchData.latitude}
              onChange={handleChange}
              className={errors.latitude ? 'error' : ''}
              placeholder="Ej: 23.113592"
              step="any"
            />
            {errors.latitude && <span className="error-text">{errors.latitude}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="longitude">Longitud:</label>
            <input
              type="number"
              id="longitude"
              name="longitude"
              value={searchData.longitude}
              onChange={handleChange}
              className={errors.longitude ? 'error' : ''}
              placeholder="Ej: -82.366592"
              step="any"
            />
            {errors.longitude && <span className="error-text">{errors.longitude}</span>}
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="radius">Radio de Búsqueda (km):</label>
          <input
            type="number"
            id="radius"
            name="radius"
            value={searchData.radius}
            onChange={handleChange}
            className={errors.radius ? 'error' : ''}
            min="1"
            max="100"
            step="1"
          />
          {errors.radius && <span className="error-text">{errors.radius}</span>}
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary">
            Buscar Estaciones
          </button>
          <button 
            type="button" 
            className="btn btn-secondary"
            onClick={handleUseCurrentLocation}
          >
            Usar Mi Ubicación
          </button>
        </div>
      </form>

      <div className="search-tips">
        <h4>Consejos de Búsqueda:</h4>
        <ul>
          <li>Las coordenadas de La Habana son aproximadamente: Lat 23.1136, Lon -82.3666</li>
          <li>Puedes usar tu ubicación actual con el botón "Usar Mi Ubicación"</li>
          <li>El radio máximo de búsqueda es de 100 km</li>
          <li>Las estaciones se muestran ordenadas por distancia</li>
        </ul>
      </div>
    </div>
  );
};

export default SearchStations;