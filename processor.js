const axios = require('axios');

module.exports = {
  async beforeScenario(ctx, ee, next) {
    try {
      const response = await axios.post('http://localhost:8080/auth/login', {
        username: 'admin',
        password: 'admin123'
      });
      ctx.vars.token = response.data.accessToken;
    } catch (err) {
      console.error("Error al obtener el token:", err.response ? err.response.data : err.message);
      ctx.vars.token = "";
    }
    return next();
  }
};
