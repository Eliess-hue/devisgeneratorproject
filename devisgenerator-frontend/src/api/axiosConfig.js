import axios from "axios";

const axiosConfig = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    headers: {
        'Content-Type': 'application/json'
    }
})

// Intercepteur - ajoute automatiquement le token JWT à chaque requête
axiosConfig.interceptors.request.use((config) => {

    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config

})

// Intercepteur de réponse — gère les tokens expirés
axiosConfig.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401 &&
            !error.config.url.includes('/api/auth/login')) {
            localStorage.removeItem('token')
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)

export default axiosConfig