import React from 'react';
import { formatChargerType } from '../utils/chargerTypeFormatter';

const StationList = ({ stations, onEdit, onDelete }) => {
  const handleDelete = async (id) => {
    const result = await onDelete(id);
    if (result.success) {
      alert(result.message);
    } else {
      alert(result.message);
    }
  };

  const handleEdit = (station) => {
    onEdit(station);
  };

  if (stations.length === 0) {
    return (
      <div className="card">
        <h3>Lista de Estaciones</h3>
        <p>No hay estaciones registradas.</p>
      </div>
    );
  }

  return (
    <div className="card">
      <h3>Lista de Estaciones ({stations.length})</h3>
      
      <div className="table-responsive">
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Dirección</th>
              <th>Coordenadas</th>
              <th>Tipo</th>
              <th>Puntos</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {stations.map((station) => (
              <tr key={station.id} className={station.status === 'AVAILABLE' ? 'available' : 'in-use'}>
                <td>{station.id}</td>
                <td>{station.address}</td>
                <td>
                  {station.latitude?.toFixed(6)}, {station.longitude?.toFixed(6)}
                </td>
                <td>
                  <span className="charger-type-badge">
                    {formatChargerType(station.chargerType)}
                  </span>
                </td>
                <td>{station.chargingPoints}</td>
                <td>
                  <span className={`status-badge status-${station.status.toLowerCase().replace('_', '-')}`}>
                    {station.status === 'AVAILABLE' ? 'Disponible' : 'En Uso'}
                  </span>
                </td>
                <td>
                  <div className="flex gap-4">
                    <button
                      className="btn btn-primary"
                      onClick={() => handleEdit(station)}
                    >
                      Editar
                    </button>
                    <button
                      className="btn btn-danger"
                      onClick={() => {
                        if (window.confirm('¿Estás seguro de que quieres eliminar esta estación?')) {
                          handleDelete(station.id);
                        }
                      }}
                    >
                      Eliminar
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="grid" style={{ marginTop: '20px' }}>
        {stations.map((station) => (
          <div key={station.id} className={`station-card ${station.status.toLowerCase()}`}>
            <h3>Estación #{station.id}</h3>
            <p><strong>Dirección:</strong> {station.address}</p>
            <p><strong>Coordenadas:</strong> {station.latitude?.toFixed(6)}, {station.longitude?.toFixed(6)}</p>
            <p><strong>Tipo:</strong> 
              <span className="charger-type-badge" style={{ marginLeft: '8px' }}>
                {formatChargerType(station.chargerType)}
              </span>
            </p>
            <p><strong>Puntos de carga:</strong> {station.chargingPoints}</p>
            <p><strong>Estado:</strong> 
              <span className={`status-badge status-${station.status.toLowerCase()}`} style={{ marginLeft: '8px' }}>
                {station.status === 'AVAILABLE' ? 'Disponible' : 'En Uso'}
              </span>
            </p>
            <div className="flex gap-4" style={{ marginTop: '15px' }}>
              <button
                className="btn btn-primary"
                onClick={() => handleEdit(station)}
              >
                Editar
              </button>
              <button
                className="btn btn-danger"
                onClick={() => {
                  if (window.confirm('¿Estás seguro de que quieres eliminar esta estación?')) {
                    handleDelete(station.id);
                  }
                }}
              >
                Eliminar
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default StationList;