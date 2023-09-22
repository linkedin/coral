import NavBar from '@/app/components/NavBar';
import DBQueryForm from '@/app/components/Forms/DBQueryForm';

// Home page also acts as database query page
export default function Home() {
  return (
    <>
      <NavBar />

      <DBQueryForm />
    </>
  );
}
