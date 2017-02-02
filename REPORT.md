# It's the Vinyl Countdown
This app offers a user friendly record selling/buying/trading experience. Using public music data from the last fm API this
app can display music info and let users decide what to do with their albums! Maintain your collection and wishlist as simple as search and click.
From your wishlist you can find which of your wanted records are available on the market. From your collection you can directly sell any
unwanted records! However, the best, never before seen feature: trading, is to my opinion the most fun and social approach of vinyl collection.
Select trade on a record when creating your advertisement and users can offer you their records in return! It only requires them to search
for a record in your advertisement and select it to send you an in app message about the offer. All in app messages are automatically generated
to keep out any malicious users! Moreover personal data is only exchanged when both parties agree on a deal, be it a price, bid or trade!

## Technical design
The sheer size and functions of this app make a lot to explain but for clarities sake I have devided the app into 5 parts:
- authentication
- selling
- marketplace
- messaging
- collection and wishlist

The authentication will discuss the basic info of how users can use the app and how their profiles are intergrated.
Selling will tell us something about the creation of objects in the marketplace, while marketplace itself is about the
buying process and how objects are retrieved matching user queries. The collection and wishlist will discuss how these lists are 
technically maintained and expanded. Finally the messaging system is explained.

### Authentication
For users to use the app they must first log in. This happens through the login activity where users enter their credentials
in the form of email, password. These accounts are created in the register activity using the same data, with a password for comparison and 
a user determined username. The usernames are kept in the database to prevent overwriting. User data is stored in UserProfile and also stored in 
the database. In order to make this data easily retrievable the structure is username: id and id : userprofile. This way everything is 
retrievable wherever needed and when edited changes all implementations of the user data. For example, when a user edits their username
their offers in the marketplace, welcometext and profile page are updated. But old messages are kept with the same username in order
to prevent confusion. 
### Selling
Selling can be done from 2 locations, a users collection and the recordSearchActivity with saleSearch method. This makes it possible 
for users to query the last fm api and click the results for the sale activity. From collection you can directly click an item and
this will open the same sale activity. The sale activity is where the magic happens. Info of the record retrieved by the api is displayed
and users can add sale info here. The first part of sale info is sale type, which will be either price, trade or bidding. The price
requires a set price to be put in, bidding needs a bidding from price and finally trade requires nothing extra. Selecting either from the
spinner updates the ui to correspond the rest of the input. The user is allowed to write a description story about their record and set
the condition in the form of a 1-5 ratingbar.
When the users presses the sell button they get a confirmation dialog. When confirmed a recordSaleInfo object is created with basic info
from the recordinfo plus all the user input data and their id (so users can see whos selling, a users sales can be retrieved and messages 
can be sent to that user).
### Marketplace
The marketplace allows users to search the offers others created. When searching from the BuySearchActivity the marketasynctask behind
it performs a search similar to the search for selling records but cross references the results with the data in the marketplace by 
querying the unique music brainz id of the record to the firebase database. These are consequently displayed in a list, showing the price,
selling user, etc.. After the search users can click any advertisement to view more details and create an offer. In this case, as was in
the selling screen, the layout is determined by the price type. Bidding lets the user submit a bid, price shows a simple buy button and
trade shows a similar search to the recordSearchActivity, this requires a query and for the user to hit search. When a result is clicked
the list dissapears and a text showing the selected record appeares. When a record is selected or a bid is put in (higher than the lowest 
at that moment) a user may press offer. This throws a dialog confirming that the user wants to message the record owner.
When successful the current bid is updated, as well as the current bids user, this is done to prevent users from spamming advertisements.
### Message
The message service is a great way of making deals in the Vinyl Countdown app. When a user replies to an offer in the marketplace an automatically
generated custom message is sent to the recordowner specifying the exact offer and bid/price/trade record. The user recieving the message
can select accept or reject in the message specification, this sends the offerer a message back with the specifications of the offer and reply.
Only, and only if an offer is accepted the email address is added to the reply so users can contact eachother over the exact exchange.
Message objects consist of contact info and specifications of the message, there are two variants the variant sent from the market and the one
sent from the message detail when a reply is selected. 
### Collection and Wishlist
These lists are maintained in respective activities, adding is done using the recordSearchActivity with colleciton and wishlist method. 
These methods determine where the objects are added in the database when clicked. Subsequently, clicking on an item in the list of collection
sends the user to the saleactivity where they can sell the record they clicked. From the Wishlist activity the user can click an item in the
list and this sends them to the marketplace and queries the records title.

## Challenges and Progress
During the development many challenges were met and overcome. I learned how to customize a lot of my code and layout. I have extensively
worked on proper json parsing, object structuring and documentation. One of the main problems was a firebase version mismatch, this required
two days to fix and I had to rebase my entire project. Finally, during the last week the emulator and phone layout weren't similar and 
the drawerlayout used for navigation didn't work on a phone at all. All these problems were fixed. The progress was really successfull in the
third week and that is when I decided to also implement the extra wishlist and collection features, as well as the messaging system.
Another important part, less programming but more design aspect, was the layout of the app, a good app should not only be easy to use
but also nice on the eye. I've tried smoothing the layout and optimizing the ui every step of the way.

## Changes and Improvements
Overall I stuck to my original design except for some minor changes is navigation and additions. 
Many of the changes I made were done to improve user experience, however if more time was at hand I would implement more basic function
with the main purpose of easing transactions between users. Such as user locations and custom messages. Many of the layouts support
landscape mode except the buy and sell activity. These have landscape disabled but in the future I would create seperate layout files
to support these which would also force me to consider state resotration better!

