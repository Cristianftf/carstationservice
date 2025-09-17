import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { formatChargerType } from '../utils/chargerTypeFormatter';

const Statistics = () => {
  const [stats, setStats] = useState({
    totalStations: 0,
    availableStations: 0,
    inUseStations: 0,
    chargerTypeDistribution: {},
    averageChargingPoints: 0,
    totalChargingPoints: 0
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchStatistics();
  }, []);

  const fetchStatistics = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Fetch all stations to calculate statistics
      const response = await axios.get('/api/charging-stations');
      const stations = response.data;

      if (stations.length === 0) {
        setStats({
          totalStations: 0,
          availableStations: 0,
          inUseStations: 0,
          chargerTypeDistribution: {},
          averageChargingPoints: 0,
          totalChargingPoints: 0
        });
        setLoading(false);
        return;
      }

      // Calculate statistics
      const availableStations = stations.filter(s => s.status === 'AVAILABLE').length;
      const inUseStations = stations.filter(s => s.status === 'IN_USE').length;
      
      const chargerTypeDistribution = stations.reduce((acc, station) => {
        acc[station.chargerType] = (acc[station.chargerType] || 0) + 1;
        return acc;
      }, {});

      const totalChargingPoints = stations.reduce((sum, station) => sum + (station.chargingPoints || 0), 0);
      const averageChargingPoints = totalChargingPoints / stations.length;

      setStats({
        totalStations: stations.length,
        availableStations,
        inUseStations,
        chargerTypeDistribution,
        averageChargingPoints: parseFloat(averageChargingPoints.toFixed(2)),
        totalChargingPoints
      });
    } catch (err) {
      setError('Error al cargar las estadísticas');
      console.error('Error fetching statistics:', err);
    } finally {
      setLoading(false);
    }
  };

  const getChargerTypePercentage = (type) => {
    if (stats.totalStations === 0) return 0;
    return ((stats.chargerTypeDistribution[type] || 0) / stats.totalStations * 100).toFixed(1);
  };

  const getStatusPercentage = (count) => {
    if (stats.totalStations === 0) return 0;
    return ((count / stats.totalStations) * 100).toFixed(1);
  };

  if (loading) {
    return (
      <div className="card">
        <h3>Estadísticas</h3>
        <div className="loading">Cargando estadísticas...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="card">
        <h3>Estadísticas</h3>
        <div className="error">{error}</div>
        <button className="btn btn-primary" onClick={fetchStatistics}>
          Reintentar
        </button>
      </div>
    );
  }

  return (
    <div className="card">
      <h3>Estadísticas del Sistema</h3>
      
      {stats.totalStations === 0 ? (
        <p>No hay estaciones registradas para mostrar estadísticas.</p>
      ) : (
        <>
          {/* Summary Cards */}
          <div className="stats-grid">
            <div className="stat-card">
              <h4>Total Estaciones</h4>
              <div className="stat-number">{stats.totalStations}</div>
            </div>
            
            <div className="stat-card available">
              <h4>Disponibles</h4>
              <div className="stat-number">{stats.availableStations}</div>
              <div className="stat-percentage">
                {getStatusPercentage(stats.availableStations)}%
              </div>
            </div>
            
            <div className="stat-card in-use">
              <h4>En Uso</h4>
              <div className="stat-number">{stats.inUseStations}</div>
              <div className="stat-percentage">
                {getStatusPercentage(stats.inUseStations)}%
              </div>
            </div>
            
            <div className="stat-card">
              <h4>Puntos Totales</h4>
              <div className="stat-number">{stats.totalChargingPoints}</div>
            </div>
            
            <div className="stat-card">
              <h4>Promedio por Estación</h4>
              <div className="stat-number">{stats.averageChargingPoints}</div>
            </div>
          </div>

          {/* Charger Type Distribution */}
          <div className="stats-section">
            <h4>Distribución por Tipo de Cargador</h4>
            <div className="distribution-grid">
              {Object.entries(stats.chargerTypeDistribution).map(([type, count]) => (
                <div key={type} className="distribution-item">
                  <span className="distribution-label">{formatChargerType(type)}:</span>
                  <span className="distribution-count">{count}</span>
                  <span className="distribution-percentage">
                    ({getChargerTypePercentage(type)}%)
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Status Distribution Chart */}
          <div className="stats-section">
            <h4>Distribución de Estado</h4>
            <div className="status-chart">
              <div 
                className="chart-bar available" 
                style={{ width: `${getStatusPercentage(stats.availableStations)}%` }}
              >
                <span>Disponible: {stats.availableStations}</span>
              </div>
              <div 
                className="chart-bar in-use" 
                style={{ width: `${getStatusPercentage(stats.inUseStations)}%` }}
              >
                <span>En Uso: {stats.inUseStations}</span>
              </div>
            </div>
          </div>

          {/* Refresh Button */}
          <div className="stats-actions">
            <button className="btn btn-primary" onClick={fetchStatistics}>
              Actualizar Estadísticas
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default Statistics;