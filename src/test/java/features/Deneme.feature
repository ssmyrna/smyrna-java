Feature: GetUsersById Test Scenarios

  Scenario: test2
    Given Prepare Url "/users/2"
    When Send Request "GET"
    Then Expected to see 200 status code
    Then Response Control
      | path    | value |
      | data.id | 2     |


  @test2
  Scenario: Success Get Single User Control2
    * Prepare Url "/users/3"
    * Send Request "GET"
    * Expected to see 200 status code
    * Response Control
      | path    | value |
      | data.id | 3     |