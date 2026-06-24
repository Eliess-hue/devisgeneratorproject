import { useEffect, useState } from 'react'

import { getDashboard } from '../api/apiDashboard.js'
import DashboardStatsCards from "../components/dashboard/DashboardStatsCards.jsx"
import DashboardRevenueChart from "../components/dashboard/DashboardRevenueChart.jsx"
import DashboardRecentQuotesTable from "../components/dashboard/DashboardRecentQuotesTable.jsx"
import DashboardPageSkeleton from "../components/skeletons/DashboardPageSkeleton.jsx"

export default function DashboardPage() {

    const [dashboard, setDashboard] =
        useState(null)

    const [loading, setLoading] =
        useState(true)

    const [error, setError] =
        useState(null)

    const loadDashboard = async () => {

        setLoading(true)

        try {

            const response =
                await getDashboard()

            setDashboard(
                response.data
            )

            setError(null)

        } catch (err) {

            console.error(err)

            setError(
                'Impossible de charger le tableau de bord'
            )

        } finally {

            setLoading(false)

        }

    }

    useEffect(() => {

        loadDashboard()

    }, [])

    if (loading) {
        return <DashboardPageSkeleton/>
    }

    if (error) {
        return <p>{error}</p>
    }

    return (
        <div className="space-y-6">

            <DashboardStatsCards
                dashboard={dashboard}
            />

            <DashboardRevenueChart
                monthlyRevenues={dashboard.monthlyRevenues}
            />

            <DashboardRecentQuotesTable
                quotes={dashboard.recentQuotes}
            />

        </div>
    )
}