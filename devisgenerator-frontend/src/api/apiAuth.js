import axiosConfig from './axiosConfig.js'

export const login = (username, password) =>
    axiosConfig.post('/api/auth/login', {
        username,
        password
    })

export const register = (username, password) =>
    axiosConfig.post('/api/auth/register', {
        username,
        password
    })