using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace KeycloakDemo.Controllers
{
    [Authorize]
    [Route("/secured")]
    [ApiController]
    public class SecuredController : ControllerBase
    {

        [HttpGet("role")]
        [Authorize(Roles = "yes_we_can")]
        public IActionResult Role()
        {
            return Ok("That's right, you can in dotnet");
        }
    }
}