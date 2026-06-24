export default function ClientsPageSkeleton() {

    return (

        <div className="space-y-6">

            <div className="flex justify-between items-center">

                <div>
                    <div className="skeleton h-10 w-48 mb-2"></div>
                    <div className="skeleton h-4 w-32"></div>
                </div>

                <div className="skeleton h-10 w-40"></div>

            </div>

            <div className="skeleton h-12 w-full"></div>

            <div className="card bg-base-200 border border-base-300">

                <div className="card-body">

                    {[...Array(5)].map((_, index) => (

                        <div
                            key={index}
                            className="flex justify-between py-4"
                        >

                            <div className="skeleton h-6 w-40"></div>
                            <div className="skeleton h-6 w-56"></div>
                            <div className="skeleton h-6 w-32"></div>
                            <div className="skeleton h-8 w-24"></div>

                        </div>

                    ))}

                </div>

            </div>

        </div>

    )

}