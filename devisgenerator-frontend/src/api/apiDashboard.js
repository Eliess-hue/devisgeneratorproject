import axiosConfig from "./axiosConfig.js";

export const getDashboard = () => {
    return axiosConfig.get('/api/dashboard')
}