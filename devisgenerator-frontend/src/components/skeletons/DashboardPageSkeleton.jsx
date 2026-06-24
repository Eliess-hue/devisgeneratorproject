export default function DashboardPageSkeleton() {

    return (

        <div className="space-y-6">

            <div className="skeleton h-10 w-56"></div>

            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">

                {[...Array(3)].map((_, index) => (

                    <div
                        key={index}
                        className="card bg-base-200 border border-base-300"
                    >

                        <div className="card-body">

                            <div className="skeleton h-4 w-24"></div>

                            <div className="skeleton h-10 w-20"></div>

                        </div>

                    </div>

                ))}

            </div>

        </div>

    )

}