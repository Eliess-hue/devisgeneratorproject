export default function QuoteDetailPageSkeleton() {

    return (

        <div className="space-y-8">

            <div className="skeleton h-10 w-40"></div>

            <div className="skeleton h-12 w-64"></div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

                <div className="card bg-base-200 border border-base-300">

                    <div className="card-body space-y-3">

                        <div className="skeleton h-6 w-40"></div>

                        <div className="skeleton h-4 w-full"></div>
                        <div className="skeleton h-4 w-3/4"></div>
                        <div className="skeleton h-4 w-1/2"></div>

                    </div>

                </div>

                <div className="card bg-base-200 border border-base-300">

                    <div className="card-body space-y-3">

                        <div className="skeleton h-6 w-40"></div>

                        <div className="skeleton h-4 w-full"></div>
                        <div className="skeleton h-4 w-3/4"></div>
                        <div className="skeleton h-4 w-1/2"></div>

                    </div>

                </div>

            </div>

            <div className="card bg-base-200 border border-base-300">

                <div className="card-body">

                    {[...Array(4)].map((_, index) => (

                        <div
                            key={index}
                            className="flex justify-between py-3"
                        >

                            <div className="skeleton h-5 w-40"></div>
                            <div className="skeleton h-5 w-16"></div>
                            <div className="skeleton h-5 w-24"></div>
                            <div className="skeleton h-5 w-24"></div>

                        </div>

                    ))}

                </div>

            </div>

            <div className="card bg-base-200 border border-base-300">

                <div className="card-body space-y-3">

                    <div className="skeleton h-6 w-40"></div>

                    <div className="skeleton h-5 w-32"></div>
                    <div className="skeleton h-5 w-32"></div>
                    <div className="skeleton h-5 w-32"></div>

                </div>

            </div>

        </div>

    )

}