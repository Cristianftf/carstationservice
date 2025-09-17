/**
 * Utilidad para formatear los tipos de cargador de manera amigable para el usuario
 */

/**
 * Formatea el tipo de cargador para mostrar al usuario
 * @param {string} chargerType - El tipo de cargador (AC, DC_FAST)
 * @returns {string} Texto formateado amigable para el usuario
 */
export const formatChargerType = (chargerType) => {
  switch (chargerType) {
    case 'AC':
      return 'AC (Corriente Alterna)';
    case 'DC_FAST':
      return 'DC (Carga Rápida)';
    default:
      return chargerType;
  }
};

/**
 * Obtiene el valor del backend a partir del texto formateado
 * @param {string} formattedType - Texto formateado para el usuario
 * @returns {string} Valor correspondiente para el backend
 */
export const getBackendChargerType = (formattedType) => {
  switch (formattedType) {
    case 'AC (Corriente Alterna)':
      return 'AC';
    case 'DC (Carga Rápida)':
      return 'DC_FAST';
    default:
      return formattedType;
  }
};

/**
 * Obtiene las opciones de tipos de cargador para usar en selects
 * @returns {Array} Array de objetos con value y label
 */
export const getChargerTypeOptions = () => [
  { value: 'AC', label: 'AC (Corriente Alterna)' },
  { value: 'DC_FAST', label: 'DC (Carga Rápida)' }
];