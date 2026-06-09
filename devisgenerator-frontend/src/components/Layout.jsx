import { Outlet } from 'react-router-dom'

import Sidebar from '../components/layout/Sidebar'
import Topbar from '../components/layout/Topbar'

export default function Layout() {

    return (

        <div className="flex min-h-screen bg-base-100">

            <Sidebar />

            <div className="flex-1 flex flex-col">

                <Topbar />

                <main className="flex-1 p-8">

                    <Outlet />

                </main>

            </div>

        </div>

    )

}