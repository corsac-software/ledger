import { createRootRoute, Outlet } from '@tanstack/react-router';

export const Route = createRootRoute({
  component: RootLayout,
});

function RootLayout() {
  return (
    <div className="min-h-screen bg-background w-screen">
      <header className="border-b w-full">
        <div className="w-full px-4 py-4">
          <h1 className="text-xl font-semibold">PregsLedger</h1>
        </div>
      </header>
      <main className="w-full px-4 py-6">
        <Outlet />
      </main>
    </div>
  );
}
