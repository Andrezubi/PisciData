using Task = System.Threading.Tasks.Task;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Persistence;
using PisciDataBackend.Infraestructure.Repositories;
using PisciDataBackend.Domain.Models;
using System;

namespace PisciDataBackend.Application.Services
{
    public class UserService
    {
        private readonly UserRepository _userRepository;

        public UserService(UserRepository userRepository)
        {
            _userRepository = userRepository;
        }

        public async Task<IEnumerable<UserDto>> GetAllAsync()
        {
            var users = await _userRepository.GetAllAsync();
            return users.Select(MapToDto);
        }

        public async Task<UserDto?> GetByIdAsync(int id)
        {
            var user = await _userRepository.GetByIdAsync(id);
            return user == null ? null : MapToDto(user);
        }

        public async Task<UserProfileDto?> GetProfileAsync(int userId)
        {
            var user = await _userRepository.GetByIdAsync(userId);
            return user == null ? null : MapToProfileDto(user);
        }

        public async Task<(UserProfileDto? Dto, List<string> Errors)> UpdateProfileAsync(
            int userId, UpdateUserProfileDto dto)
        {
            var errors = UserValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var user = await _userRepository.GetByIdAsync(userId);
            if (user == null)
                return (null, errors);

            var firstName = dto.FirstName.Trim();
            var lastName = dto.LastName.Trim();
            var phone = dto.Phone.Trim();

            if (await _userRepository.PhoneExistsForOtherUserAsync(phone, userId))
                return (null, new List<string> { "El teléfono ya está registrado por otro usuario." });

            user.FirstName = firstName;
            user.LastName = lastName;
            user.Phone = phone;
            user.UserId = userId;

            await _userRepository.UpdateAsync(user);
            return (MapToProfileDto(user), errors);
        }

        public async Task<(UserDto? Dto, List<string> Errors)> CreateAsync(CreateUserDto dto)
        {
            var errors = UserValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var user = new User
            {
                FirstName = dto.FirstName,
                LastName = dto.LastName,
                Phone = dto.Phone,
                PasswordHash = BCrypt.Net.BCrypt.HashPassword(dto.Password),
                Role = dto.Role,
                CreatedAt = DateTime.UtcNow,
                IsActive = true
            };

            await _userRepository.AddAsync(user);
            return (MapToDto(user), errors);
        }

        public async Task<User?> GetEntityByPhoneAsync(string phone)
        {
            return await _userRepository.GetByPhoneAsync(phone);
        }

        public async Task UpdateLastLoginAsync(User user)
        {
            user.LastLoginAt = DateTime.UtcNow;
            await _userRepository.UpdateAsync(user);
        }

        public async Task<(UserDto? Dto, List<string> Errors)> UpdateAsync(int id, UpdateUserDto dto)
        {
            var errors = UserValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var user = await _userRepository.GetByIdAsync(id);
            if (user == null)
                return (null, errors);

            user.FirstName = dto.FirstName;
            user.LastName = dto.LastName;
            user.Phone = dto.Phone;
            user.Role = dto.Role;
            user.UpdatedAt = DateTime.UtcNow;

            await _userRepository.UpdateAsync(user);
            return (MapToDto(user), errors);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var user = await _userRepository.GetByIdAsync(id);
            if (user == null)
                return false;

            await _userRepository.DeleteAsync(user);
            return true;
        }

        private static UserDto MapToDto(User user)
        {
            return new UserDto
            {
                Id = user.Id,
                FirstName = user.FirstName,
                LastName = user.LastName,
                Phone = user.Phone,
                Role = user.Role,
                LastLoginAt = user.LastLoginAt,
                CreatedAt = user.CreatedAt,
                UpdatedAt = user.UpdatedAt,
                IsActive = user.IsActive
            };
        }

        private static UserProfileDto MapToProfileDto(User user)
        {
            return new UserProfileDto
            {
                Id = user.Id,
                FirstName = user.FirstName,
                LastName = user.LastName,
                Phone = user.Phone,
                Role = user.Role,
                LastLoginAt = user.LastLoginAt,
                CreatedAt = user.CreatedAt,
                UpdatedAt = user.UpdatedAt,
                IsActive = user.IsActive
            };
        }
    }
}
