cd C:\jGraphed2004
git init
git add .
git commit -m "Baseline: JGraphEd restored and buildable on modern Java"
git remote remove origin
git remote add origin https://github.com/kruin/JGraphEd.git
git fetch origin
git ls-remote --heads origin
git branch -M main
git pull origin main --allow-unrelated-histories
git push -u origin main