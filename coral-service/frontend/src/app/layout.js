import './globals.css';
import { Inter } from 'next/font/google';

const inter = Inter({ subsets: ['latin'] });

export const metadata = {
  title: 'Coral',
  description:
    'Coral is a translation, analysis, and query rewrite engine for SQL and other relational languages.',
};

export default function RootLayout({ children }) {
  return (
    <html lang='en' className='h-max bg-white'>
      <body className={`h-max ${inter.className} font-sans`}>{children}</body>
    </html>
  );
}
