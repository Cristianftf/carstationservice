import React, { useState, useEffect } from 'react';

const StationForm = ({ station, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    address: '',
    latitude: '',
    longitude: '',
    chargerType: 'AC',
    chargingPoints: 1,
    status: 'AVAILABLE'
  });

  const [errors, setErrors] = useState({});
  const [isEditing] = useState(!!station);

  useEffect(() => {
    if (station) {
      setFormData({
        address: station.address || '',
        latitude: station.latitude?.toString() || '',
        longitude: station.longitude?.toString() || '',
        chargerType: station.chargerType || 'AC',
        chargingPoints: station.chargingPoints || 1,
        status: station.status || 'AVAILABLE'
      });
    }
  }, [station]);

  const validateForm = () => {
    const newErrors = {};

    if (!formData.address.trim()) {
      newErrors.address = 'La dirección es requerida';
    }

    if (!formData.latitude) {
      newErrors.latitude = 'La latitud es requerida';
    } else if (isNaN(formData.latitude) || formData.latitude < -90 || formData.latitude > 90) {
      newErrors.latitude = 'La latitud debe ser un número entre -90 y 90';
    }

    if (!formData.longitude) {
      newErrors.longitude = 'La longitud es requerida';
    } else if (isNaN(formData.longitude) || formData.longitude < -180 || formData.longitude > 180) {
      newErrors.longitude = 'La longitud debe ser un número entre -180 y 180';
    }

    if (!formData.chargingPoints || formData.chargingPoints < 1) {
      newErrors.chargingPoints = 'Debe tener al menos 1 punto de carga';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) {
      const dataToSave = {
        ...formData,
        latitude: parseFloat(formData.latitude),
        longitude: parseFloat(formData.longitude),
        chargingPoints: parseInt(formData.chargingPoints)
      };
      
      if (station && station.id) {
        dataToSave.id = station.id;
      }
      
      onSave(dataToSave);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
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

  return (
    <div className="card">
      <h3>{isEditing ? 'Editar Estación' : 'Crear Nueva Estación'}</h3>
      
      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label htmlFor="address">Dirección:</label>
          <input
            type="text"
            id="address"
            name="address"
            value={formData.address}
            onChange={handleChange}
            className={errors.address ? 'error' : ''}
            placeholder="Ej: Calle Principal #123"
          />
          {errors.address && <span className="error-text">{errors.address}</span>}
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="latitude">Latitud:</label>
            <input
              type="number"
              id="latitude"
              name="latitude"
              value={formData.latitude}
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
              value={formData.longitude}
              onChange={handleChange}
              className={errors.longitude ? 'error' : ''}
              placeholder="Ej: -82.366592"
              step="any"
            />
            {errors.longitude && <span className="error-text">{errors.longitude}</span>}
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="chargerType">Tipo de Cargador:</label>
            <select
              id="chargerType"
              name="chargerType"
              value={formData.chargerType}
              onChange={handleChange}
            >
              <option value="AC">AC (Corriente Alterna)</option>
              <option value="DC_FAST">DC (Carga Rápida)</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="chargingPoints">Puntos de Carga:</label>
            <input
              type="number"
              id="chargingPoints"
              name="chargingPoints"
              value={formData.chargingPoints}
              onChange={handleChange}
              className={errors.chargingPoints ? 'error' : ''}
              min="1"
              max="20"
            />
            {errors.chargingPoints && <span className="error-text">{errors.chargingPoints}</span>}
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="status">Estado:</label>
          <select
            id="status"
            name="status"
            value={formData.status}
            onChange={handleChange}
          >
            <option value="AVAILABLE">Disponible</option>
            <option value="IN_USE">En Uso</option>
          </select>
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary">
            {isEditing ? 'Actualizar' : 'Crear'} Estación
          </button>
          <button type="button" className="btn btn-secondary" onClick={onCancel}>
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
};

export default StationForm;