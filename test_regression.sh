CUR_COMMIT=`git rev-parse HEAD`
CUR_COMMIT_TITLE=`git log --format=%s -n 1 $CUR_COMMIT`

echo ""
echo "Revert to origin/master"
exists=`git show-ref refs/heads/regresssion_test_base`
if [ -n "$exists" ]; then
   git branch -D regresssion_test_base
fi

git checkout origin/master -b regresssion_test_base
ligradle clean build idea

echo ""
echo "Run view translations on origin/master"
ligradle translateAll -PresultDir=../build/before

echo ""
echo "Checkout your changes'"
git checkout master
ligradle clean build idea

echo ""
echo "Run view translations on your changes"
ligradle translateAll -PresultDir=../build/after -Pinclude=../build/before/successes.txt

if diff build/before/prestoSql.txt build/after/prestoSql.txt > build/sql-diff.txt
then
    echo "NO REGRESSION! YOUR CHANGES ARE SAFE TO COMMIT"
else
    echo "YOUR CHANGES MAY INTRODUCE REGRESSION. Please check after/failures.txt for failed datasets, and run diff on build/before/prestoSql.txt and build/after/prestoSql.txt for more details"
fi