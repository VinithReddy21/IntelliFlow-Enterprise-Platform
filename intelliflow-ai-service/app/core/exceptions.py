from fastapi import Request, status
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError
from app.schemas.response import ApiResponse

class CustomBaseException(Exception):
    """Base exception for AI microservice domain errors."""
    def __init__(self, message: str, status_code: int = status.HTTP_400_BAD_REQUEST):
        self.message = message
        self.status_code = status_code
        super().__init__(message)

async def custom_base_exception_handler(request: Request, exc: CustomBaseException) -> JSONResponse:
    """Interceptor for domain business exceptions."""
    response_payload = ApiResponse[None](
        status="error",
        message=exc.message,
        data=None
    )
    return JSONResponse(
        status_code=exc.status_code,
        content=response_payload.model_dump(mode="json")
    )

async def validation_exception_handler(request: Request, exc: RequestValidationError) -> JSONResponse:
    """Interceptor for Pydantic request validation errors."""
    errors = {}
    for error in exc.errors():
        field_name = ".".join([str(loc) for loc in error["loc"] if loc != "body"])
        errors[field_name] = error["msg"]

    response_payload = ApiResponse[dict](
        status="error",
        message="Request parameter validation failed",
        data={"validation_errors": errors}
    )
    return JSONResponse(
        status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
        content=response_payload.model_dump(mode="json")
    )

async def global_exception_handler(request: Request, exc: Exception) -> JSONResponse:
    """Fallback handler for unhandled internal server exceptions."""
    response_payload = ApiResponse[None](
        status="error",
        message="An unexpected internal server error occurred",
        data=None
    )
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content=response_payload.model_dump(mode="json")
    )
