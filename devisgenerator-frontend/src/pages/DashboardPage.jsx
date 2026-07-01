import useDashboard from "../hooks/useDashboard"

import DashboardStatsCards from "../components/dashboard/DashboardStatsCards.jsx"
import DashboardRevenueChart from "../components/dashboard/DashboardRevenueChart.jsx"
import DashboardRecentQuotesTable from "../components/dashboard/DashboardRecentQuotesTable.jsx"
import DashboardPageSkeleton from "../components/skeletons/DashboardPageSkeleton.jsx"

export default function DashboardPage() {

    const {
        dashboard,
        loading,
        error
    } = useDashboard()

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