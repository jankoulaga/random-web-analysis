A random web page analysis application

Query this web service 100 times: 
http://www.randomwebsite.com/cgi-bin/random.pl 

The response contains a short HTML document with one link inside. Query this link and store link URL, as well as the returned HTTP response codes in a suitable data structure.
The application should output a list of all link URLs ordered by the first character of the domain name (ignore http:// and www prefixes) and also print the related HTTP Code, if it’s not 200.

The project does all the work using Actor system to create an Actor which gathers all the results by creating child actors which do the actual url visit.
