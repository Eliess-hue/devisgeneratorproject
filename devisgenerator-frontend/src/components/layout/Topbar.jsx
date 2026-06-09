import { useLocation } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

export default function Topbar() {

    const location = useLocation()
    const { username } = useAuth()

    const pageTitles = {
        '/dashboard': 'Tableau de bord',
        '/clients': 'Clients',
        '/quotes': 'Devis'
    }

    const currentTitle =
        pageTitles[location.pathname] || 'DevisApp'

    return (

        <header className="h-20 border-b border-base-300 flex items-center justify-between px-8">

            <h1 className="text-3xl font-bold">
                {currentTitle}
            </h1>

            <span>
                  {username}
            </span>

        </header>

    )

}