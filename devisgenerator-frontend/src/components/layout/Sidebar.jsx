import {NavLink} from 'react-router-dom'

import UserProfile from './UserProfile'

export default function Sidebar() {

    return (

        <aside className="w-64 bg-base-200 border-r border-base-300 flex flex-col">

            {/* Logo */}
            <div className="p-6 flex items-center gap-3">

                <div className="w-10 h-10 rounded-full bg-primary"></div>

                <span className="font-bold text-lg">
                    DevisApp
                </span>

            </div>

            {/* Navigation */}
            <nav className="flex-1 px-3">

                <NavLink
                    to="/dashboard"
                    className={({ isActive }) =>
                        `block px-4 py-3 rounded-lg mb-2 ${
                            isActive
                                ? 'bg-primary text-primary-content'
                                : 'hover:bg-base-300'
                        }`
                    }
                >
                    Dashboard
                </NavLink>

                <NavLink
                    to="/clients"
                    className={({ isActive }) =>
                        `block px-4 py-3 rounded-lg mb-2 ${
                            isActive
                                ? 'bg-primary text-primary-content'
                                : 'hover:bg-base-300'
                        }`
                    }
                >
                    Clients
                </NavLink>

                <NavLink
                    to="/quotes"
                    className={({ isActive }) =>
                        `block px-4 py-3 rounded-lg ${
                            isActive
                                ? 'bg-primary text-primary-content'
                                : 'hover:bg-base-300'
                        }`
                    }
                >
                    Devis
                </NavLink>

            </nav>

            {/* Profil */}
            <div className="p-4 border-t border-base-300">

                <UserProfile />

            </div>

        </aside>

    )

}