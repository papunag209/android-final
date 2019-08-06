# android-final
final project for Android Spring Course Free Uni

MainActivity-ში არსებული WifiP2Pmanager.discoverPeers() -ის მეშვეობით იწყება peer ების ძებნა რის შემდეგაც WifiBroadcastReceiver-ში მოგვდის peer-ების სია რომელსაც PeerListListener-ით ვუსმენთ და ცვლილებისას MutableLiveData<Collection<WifiP2pDevice>>-ს ვანახლებთ ახალი სიით. განახლებული სიის მიღების შემდეგ ვამოწმებთ რომელიმე peer-თან გვაქვს თუ არა კავშირი. თუ გვაქვს გადავდივართ ჩატში თუ არა - ვაჩვენებთ peer-ების სიას MainFragment-ში. რომელიმე peer-ის მონიშვნის შემთხვევაში ვიღებთ შესაბამის ინდექსზე მდებარე WifiP2pDevice-ს და ვცდილობთ მასთან დაგაკვშირებას. თუ წარმატებულად დავუკავშირდით WifiBroadcastReceiver გვამცნობს კავშირის შესახებ connectionInfoListener-ში. connectionInfoListener-ის onConnectionInfoAvailable-ში სანამ ახალ კავშირს გავხსნით ყველა გამოსაყენებელ სოკეტს ვხურავთ რომ თავიდან ავიცილოთ დაკავებული პორტის გამოყენება. შემდეგ ვამოწმებთ ვინ არის groupOwner და მის მხარეს ვსტარტავთ ServerSideThread, ხოლო მეორე შემთხვევაში ClientSideThread-ს. თავის მხრივ ორივე სტარტავს sendAndReceive ნაკადს. ეს 3 მარტივი კლასი არის პასუხისმგებელი კომუნიკაციაზე. თუ მიღებული მესიჯის დესერიალიზაცია ვერ მოხდა ჩვენი აპლიკაციის მოდელის მიხედვით(ანუ peer-მა რამის გამოგზავნა ცადა wifi direct ით ჩვენი აპლიკაციის გარედან) მაშინ ქონექშენი წყდება უსაფრთხოების მიზნით.
  
  
ბაზაში ინფორმაცია განაწილებულია 2 ცხრილში ერთი ინახავს სესიებს პარტნიორის მისამართის მიხედვით მეორე ინახავს ინდივიდუალურ მესიჯებს
სესიის ცხრილი ინახავს: სესიის უნიკაურ idს, სესიის დაწყების დროს, პერტნიორის სახელს, პარტნიორის მისამართს.
მესიჯის ცხრილი ინახავს: მესიჯის id, სესიის id, მესიჯის გაგზავნის დრო, მესიჯის სტატუსი(მიღებული, გაგზავნილი, გასაგზავნი), მესიჯის ტექსტი.
დატაზე წვდომა განხორციელებულია LiveDataობიექტების საშუალებით ხოლო ჩაწერები Completeable და Single ქივორდების გამოყენებით(rxJava ს საშუალებით ხელით რომ არ იქყოს სრედები საწერი). ბოლო ნახევარ საათში მგვიწია ყველა ქოლბექის asysnTaskებად გადაკეთება იმიტო რო მეინ სრედზე ვერ ავამუშავეთ.

ვიუების საკონტროლოდ გამოყენებულია MVVM + NavigationComponent პატერნი ანუ ვიუებს ურთიერთობა აქვთ მხოლოდ ბაზასთან გარდა იმ შემთხვევისა როცა მთავარ ფრაგმენტსა და მთავარ აქტივიტის შორის ხდება ურთიერთობა WiFi დივაისებისებზე ინფორმაციის მიმოცვლის მიზნით.
მესიჯის გაგზავნის დროსაც ფრაგმენტი მესიჯს წერს ბაზაში და მეინ აქტივიტი თვითონ აგზავნის მესიჯს ფრაგმენტთან პირდაპირი კავშირის გარეშე.
