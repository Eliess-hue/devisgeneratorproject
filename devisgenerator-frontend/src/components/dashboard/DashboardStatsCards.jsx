export default function DashboardStatsCards({dashboard}) {

    return (

        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">

            <div className="card bg-base-200 shadow-sm">
                <div className="card-body">
                    <h3 className="text-sm opacity-70">
                        Chiffre d'affaires
                    </h3>

                    <p className="text-3xl font-bold">
                        {dashboard.totalRevenue} €
                    </p>
                </div>
            </div>

            <div className="card bg-base-200 shadow-sm">
                <div className="card-body">
                    <h3 className="text-sm opacity-70">
                        Nombre de devis
                    </h3>

                    <p className="text-3xl font-bold">
                        {dashboard.totalQuotes}
                    </p>
                </div>
            </div>

            <div className="card bg-base-200 shadow-sm">
                <div className="card-body">
                    <h3 className="text-sm opacity-70">
                        En attente
                    </h3>

                    <p className="text-3xl font-bold">
                        {dashboard.pendingQuotes}
                    </p>
                </div>
            </div>

            <div className="card bg-base-200 shadow-sm">
                <div className="card-body">
                    <h3 className="text-sm opacity-70">
                        Acceptés
                    </h3>

                    <p className="text-3xl font-bold text-blue-500">
                        {dashboard.acceptedQuotes}
                    </p>
                </div>
            </div>

        </div>

    )

}