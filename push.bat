cd C:\jGraphed2004
git init
git add .
git commit -m "Baseline: JGraphEd restored and buildable on modern Java"
git remote remove origin
git remote add origin https://github.com/kruin/JGraphEd.git
git branch -M main
git push -u origin main --force