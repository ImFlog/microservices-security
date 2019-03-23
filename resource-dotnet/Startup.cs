using System.Collections.Generic;
using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.IdentityModel.Logging;
using Microsoft.IdentityModel.Tokens;
using Newtonsoft.Json;

namespace KeycloakDemo
{
    public class Startup
    {
        public Startup(IConfiguration configuration, IHostingEnvironment environment)
        {
            Configuration = configuration;
            Environment = environment;
        }

        public IConfiguration Configuration { get; }
        public IHostingEnvironment Environment { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            IdentityModelEventSource.ShowPII = true;
            
            services.AddMvc().SetCompatibilityVersion(CompatibilityVersion.Version_2_2);
            
            services.AddTransient<IClaimsTransformation, ClaimsTransformer>();
            
            services.AddAuthentication(options =>
            {
                options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
                options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;

            }).AddJwtBearer(o =>
            {
                o.Authority = Configuration["Jwt:Authority"];
                o.Audience = Configuration["Jwt:Audience"];
                o.RequireHttpsMetadata = false;
                o.TokenValidationParameters = new TokenValidationParameters
                {
                    RoleClaimType = ClaimTypes.Role
                };

                o.Events = new JwtBearerEvents
                {
                    OnAuthenticationFailed = c =>
                    {
                        c.NoResult();
                        c.Response.StatusCode = 500;
                        c.Response.ContentType = "text/plain";

                        if (Environment.IsDevelopment())
                        {
                            return c.Response.WriteAsync(c.Exception.ToString());
                        }
                        
                        return c.Response.WriteAsync("An error occured processing your authentication.");
                    }
                };
            });
            
            services.AddCors();
        }
        
        public class ClaimsTransformer : IClaimsTransformation
        {
            public Task<ClaimsPrincipal> TransformAsync(ClaimsPrincipal principal)
            {
                var claimsIdentity = (ClaimsIdentity)principal.Identity;

                // flatten realm_access because Microsoft identity model doesn't support nested claims
                // by map it to Microsoft identity model, because automatic JWT bearer token mapping already processed here
                if (claimsIdentity.IsAuthenticated && claimsIdentity.HasClaim(claim => claim.Type == "realm_access"))
                {
                    var realmAccessClaim = claimsIdentity.FindFirst(claim => claim.Type == "realm_access");
                    var realmAccessAsDict = JsonConvert.DeserializeObject<Dictionary<string, string[]>>(realmAccessClaim.Value);
                    if (realmAccessAsDict["roles"] != null)
                    {
                        foreach (var role in realmAccessAsDict["roles"])
                        {
                            claimsIdentity.AddClaim(new Claim(ClaimTypes.Role, role));
                        }
                    }
                }

                return Task.FromResult(principal);
            }
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }
            
            app.UseAuthentication();
            
            app.UseCors(builder =>
                builder.WithOrigins("*").AllowAnyHeader());
            
            app.UseMvc();
        }
    }
}