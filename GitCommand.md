## ✅ replace the master branch with the kafka-basic branch 
— meaning you want master to match exactly what’s in kafka-basic

```bash
git checkout kafka-basic
git pull origin kafka-basic            # optional
git branch -f master kafka-basic # DANGEROUS, use with caution
git checkout master
git push origin master --force
```