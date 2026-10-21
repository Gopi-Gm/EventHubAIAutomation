@multipleevents
Feature: EventHub Multiple Events Creation and Management

  Background:
    Given I am logged in as "prem@yopmail.com" with password "Automation@2026"

  # ------------------------------------------------------------------ #
  # Scenario 1: Create all 10 events from the JSON dataset using        #
  #             a Scenario Outline — one row per event                  #
  # ------------------------------------------------------------------ #
  Scenario Outline: Create a new event with full details - <title>
    When I create a full event with title "<title>" description "<description>" category "<category>" date "<eventDate>" time "<eventTime>" location "<venue>" city "<city>" capacity <totalSeats> price <price> and image "<imageUrl>"
    Then the response status code should be 201
    And the event is created successfully

    Examples:
      | title                           | description                                                                                          | category        | eventDate  | eventTime | venue                                    | city        | totalSeats | price | imageUrl                                      |
      | Tech Summit 2026                | A premier technology conference.                                                                     | Conference      | 2027-06-15 | 09:00     | Bangalore International Centre           | Bangalore   | 500        | 1500  | https://example.com/banner.jpg                |
      | CodeSprint Hackathon 2026       | A 48-hour intense coding marathon to build innovative solutions for smart cities.                    | Hackathon       | 2027-11-15 | 00:00     | Symbiosis International University       | Pune        | 400        | 0     | https://example.com/hackathon.jpg             |
      | UX/UI Design Expo 2026          | Showcasing the future of digital product design, prototyping tools, and user research methodologies. | Expo            | 2027-12-01 | 10:00     | Pragati Maidan                           | New Delhi   | 1500       | 800   | https://example.com/design-expo.jpg           |
      | Data Science & Analytics Seminar| Learn predictive modeling, big data tools, and real-world AI applications from industry leaders.     | Seminar         | 2027-07-25 | 09:00     | IIT Madras Research Park                 | Chennai     | 150        | 750   | https://example.com/data-science.jpg          |
      | Indie Game Dev Con 2026         | A vibrant celebration of independent game developers, featuring live demos, panels, and networking.  | Convention      | 2027-08-30 | 11:00     | Science City Kolkata                     | Kolkata     | 800        | 600   | https://example.com/gamedev.jpg               |
      | Blockchain Summit 2027           | Exploring decentralized finance DeFi, smart contracts, and the next generation of the internet.     | Summit          | 2027-09-18 | 09:30     | Gujarat University Convention Centre     | Ahmedabad   | 600        | 2500  | https://example.com/web3-summit.jpg           |
      | Hands-on Robotics Workshop      | Build, wire, and program your first autonomous robot using Arduino and Raspberry Pi.                 | Workshop        | 2027-10-05 | 10:00     | Malaviya National Institute of Technology| Jaipur      | 50         | 1500  | https://example.com/robotics-workshop.jpg     |
      | Sustainable Green Tech Fair     | Innovations in renewable energy, electric mobility, and eco-friendly technology solutions.           | Fair            | 2027-11-10 | 08:00     | Leisure Valley Grounds                   | Chandigarh  | 2000       | 300   | https://example.com/greentech-fair.jpg        |
      | Women in Tech Leadership Forum  | Empowering women in STEM through mentorship, keynote speeches, and career advancement strategies.    | Forum           | 2027-03-08 | 09:00     | Taj Lands End                            | Mumbai      | 350        | 1200  | https://example.com/women-in-tech.jpg         |
      | Open Source Developers Day      | A community-driven event celebrating open source contributions, featuring lightning talks and code sprints. | Community Event | 2027-05-22 | 10:30     | Infosys Campus                           | Mysore      | 1000       | 0     | https://example.com/opensource-day.jpg        |

  # ------------------------------------------------------------------ #
  # Scenario 2: Verify free events (price = 0) can be created           #
  # ------------------------------------------------------------------ #
  Scenario: Create a free event with zero price succeeds
    When I create a full event with title "CodeSprint Hackathon 2026" description "A 48-hour intense coding marathon." category "Hackathon" date "2027-11-15" time "00:00" location "Symbiosis International University" city "Pune" capacity 400 price 0 and image "https://example.com/hackathon.jpg"
    Then the response status code should be 201
    And the event is created successfully

  # ------------------------------------------------------------------ #
  # Scenario 3: Create a high-capacity event (2000 seats)               #
  # ------------------------------------------------------------------ #
  Scenario: Create a large-scale event with high seat capacity
    When I create a full event with title "Sustainable Green Tech Fair" description "Innovations in renewable energy and eco-friendly tech." category "Fair" date "2027-11-10" time "08:00" location "Leisure Valley Grounds" city "Chandigarh" capacity 2000 price 300 and image "https://example.com/greentech-fair.jpg"
    Then the response status code should be 201
    And the event is created successfully

  # ------------------------------------------------------------------ #
  # Scenario 4: Create a limited-seat workshop (50 seats)               #
  # ------------------------------------------------------------------ #
  Scenario: Create a small capacity workshop with limited seats
    When I create a full event with title "Hands-on Robotics Workshop" description "Build your first autonomous robot using Arduino." category "Workshop" date "2027-10-05" time "10:00" location "Malaviya National Institute of Technology" city "Jaipur" capacity 50 price 1500 and image "https://example.com/robotics-workshop.jpg"
    Then the response status code should be 201
    And the event is created successfully

  # ------------------------------------------------------------------ #
  # Scenario 5: Create event then filter by its category                #
  # ------------------------------------------------------------------ #
  Scenario: Create a Conference event and filter events by Conference category
    When I create a full event with title "Tech Summit 2026" description "A premier technology conference." category "Conference" date "2027-06-15" time "09:00" location "Bangalore International Centre" city "Bangalore" capacity 500 price 1500 and image "https://example.com/banner.jpg"
    And the event is created successfully
    When I get events filtered by category "Conference"
    Then the response status code should be 200
    And the response body should contain "Tech Summit 2026"

  # ------------------------------------------------------------------ #
  # Scenario 6: Create event then filter events by city                 #
  # ------------------------------------------------------------------ #
  Scenario: Create an event in Mumbai and filter events by city Mumbai
    When I create a full event with title "Women in Tech Leadership Forum" description "Empowering women in STEM." category "Forum" date "2027-03-08" time "09:00" location "Taj Lands End" city "Mumbai" capacity 350 price 1200 and image "https://example.com/women-in-tech.jpg"
    And the event is created successfully
    When I get events filtered by city "Mumbai"
    Then the response status code should be 200
    And the response body should contain "Women in Tech Leadership Forum"

  # ------------------------------------------------------------------ #
  # Scenario 7: Create event then search by keyword in title            #
  # ------------------------------------------------------------------ #
  Scenario: Create a Blockchain event and search by keyword Blockchain
    When I create a full event with title "Blockchain Summit 2027" description "Exploring DeFi, smart contracts, and next-gen internet." category "Summit" date "2027-09-18" time "09:30" location "Gujarat University Convention Centre" city "Ahmedabad" capacity 600 price 2500 and image "https://example.com/web3-summit.jpg"
    And the event is created successfully
    When I search events with query "Blockchain"
    Then the response status code should be 200
    And the response body should contain "Blockchain Summit 2027"

  # ------------------------------------------------------------------ #
  # Scenario 8: Create event then retrieve it by ID                     #
  # ------------------------------------------------------------------ #
  Scenario: Create a Seminar event and retrieve it by its generated ID
    When I create a full event with title "Data Science & Analytics Seminar" description "Predictive modeling and AI applications." category "Seminar" date "2027-07-25" time "09:00" location "IIT Madras Research Park" city "Chennai" capacity 150 price 750 and image "https://example.com/data-science.jpg"
    And the event is created successfully
    When I fetch the created event by ID
    Then the response status code should be 200
    And the response body should contain "Data Science"

  # ------------------------------------------------------------------ #
  # Scenario 9: Create event then update its title                      #
  # ------------------------------------------------------------------ #
  Scenario: Create an Expo event and update its title
    When I create a full event with title "UX/UI Design Expo 2026" description "Showcasing the future of digital product design." category "Expo" date "2027-12-01" time "10:00" location "Pragati Maidan" city "New Delhi" capacity 1500 price 800 and image "https://example.com/design-expo.jpg"
    And the event is created successfully
    When I update the event title to "UX/UI Design Expo 2026 - Updated"
    Then the response status code should be 200

  # ------------------------------------------------------------------ #
  # Scenario 10: Create event and delete it — full lifecycle            #
  # ------------------------------------------------------------------ #
  Scenario: Create a Convention event and delete it successfully
    When I create a full event with title "Indie Game Dev Con 2026" description "Celebrating indie game developers." category "Convention" date "2027-08-30" time "11:00" location "Science City Kolkata" city "Kolkata" capacity 800 price 600 and image "https://example.com/gamedev.jpg"
    And the event is created successfully
    When I delete the created event
    Then the response status code should be 200

  # ------------------------------------------------------------------ #
  # Scenario 11: Create event fails when title is missing               #
  # ------------------------------------------------------------------ #
  Scenario: Create event fails when title is empty
    When I create a full event with title "" description "Valid description" category "Conference" date "2027-06-15" time "09:00" location "Some Venue" city "Bangalore" capacity 100 price 500 and image "https://example.com/img.jpg"
    Then the response status code should be 400

  # ------------------------------------------------------------------ #
  # Scenario 12: Create event fails when category is missing            #
  # ------------------------------------------------------------------ #
  Scenario: Create event fails when category is empty
    When I create a full event with title "Valid Title" description "Valid description" category "" date "2027-06-15" time "09:00" location "Some Venue" city "Bangalore" capacity 100 price 500 and image "https://example.com/img.jpg"
    Then the response status code should be 400
