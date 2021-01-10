## Parsing structured text into data structures in Scala

1. Parsing text format to AST object (using parser combinators?)
2. Mapping AST to Scala objects
3. Decoder as typeclass
4. Error handling and reporting 
5. Automatic derivation of typeclasses using Magnolia 

## IKVF 

Imaginary key value format:

```
id:1 userName:John score:20000 
apiKey:000-12dafa11 

id:2 userName:winner2018 registrationDate:2018-06-11 score:12
 
id:3
userName:winner2018 
score:12323 
apiKey:bbb-foo-bar
```

