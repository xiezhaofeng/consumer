package com.jers.config;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.omg.CORBA.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;

@ControllerAdvice
public class BaseExceptionHandler extends ResponseEntityExceptionHandler {

	private static Logger LOG = LoggerFactory.getLogger(BaseExceptionHandler.class);
	
	@Autowired
	private org.springframework.cloud.client.discovery.DiscoveryClient discoveryClient;

	@ExceptionHandler({ SQLException.class, ParseException.class })
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public String handleSQLException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		handleLog(request, ex);
		return showJson(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请联系客服人员service exception!", request);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public String handleValidationException(HttpServletRequest request, HttpServletResponse response, ConstraintViolationException ex) {
		handleLog(request, ex);
		Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
		StringBuffer validBuff = new StringBuffer();
		for (ConstraintViolation<?> constraintViolation : constraintViolations) {
			validBuff.append("[");
			// validBuff.append("value:").append(constraintViolation.getInvalidValue()).append("-");
			validBuff.append(constraintViolation.getMessage()).append("]");
		}
		return showJson(1001, "parameter validation failure" + validBuff.toString(), request);
	}
	@ExceptionHandler(ConnectException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public String handleValidationException(HttpServletRequest request, HttpServletResponse response, ConnectException ex) {
		handleLog(request, ex);
		discoveryClient.getServices();
		return showJson(1001, "连接失败，请重试", request);
	}

	@ExceptionHandler(NullPointerException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public String handleNullException(HttpServletRequest request, HttpServletResponse response, NullPointerException ex) {
		handleLog(request, ex);
		return showJson(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请联系客服人员NullPointer!", request);
	}


	@ExceptionHandler(SocketTimeoutException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public String handleRequestTimeoutException(HttpServletRequest request, SocketTimeoutException ex) {
		handleLog(request, ex);
		return showJson(500, "请联系客服人员Request Socket Timeout", request);
	}


	@ExceptionHandler(JSONException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public String handleRuntimeException(HttpServletRequest request, JSONException ex) {
		handleLog(request, ex);
		return showJson(500, "请联系客服人员jsonParse", request);
	}

	// /**
	// *
	// * @Title handleServerRejectException
	// * @Description 缺少请求参数
	// * @param request
	// * @param ex
	// * @return String
	// * @throws
	// */
	// @ExceptionHandler(MissingServletRequestParameterException.class)
	// @ResponseStatus(value = HttpStatus.OK)
	// @ResponseBody
	// public String handleRequestException(HttpServletRequest request,
	// MissingServletRequestParameterException ex) {
	// handleLog(request, ex);
	// ResultMessage message = new ResultMessage(
	// HttpStatus.BAD_REQUEST.value(),
	// "required_parameter_is_not_present");
	// return JSONUtil.toJson(message);
	// }
	//
	// /**
	// *
	// * @Title handleRequestException
	// * @Description 参数解析失败
	// * @param request
	// * @param ex
	// * @return String
	// * @throws
	// */
	// @ExceptionHandler(HttpMessageNotReadableException.class)
	// @ResponseStatus(value = HttpStatus.OK)
	// @ResponseBody
	// public String handleRequestException(HttpServletRequest request,
	// HttpMessageNotReadableException ex) {
	// handleLog(request, ex);
	// ResultMessage message = new ResultMessage(
	// HttpStatus.BAD_REQUEST.value(), "could_not_read_json");
	// return JSONUtil.toJson(message);
	// }
	//
	// /**
	// *
	// * @Title handleRequestException
	// * @Description 400 - Bad Request
	// * @param request
	// * @param ex
	// * @return String
	// * @throws
	// */
	// @ExceptionHandler( MethodArgumentNotValidException.class )
	// @ResponseStatus(value = HttpStatus.OK)
	// @ResponseBody
	// public String handleRequestException(HttpServletRequest request,
	// MethodArgumentNotValidException ex) {
	// handleLog(request, ex);
	// ResultMessage message = new ResultMessage(
	// HttpStatus.BAD_REQUEST.value(), "validation_exception");
	// return JSONUtil.toJson(message);
	// }
	//
	// /**
	// * @Description 400 - Bad Request
	// * @param request
	// * @param ex
	// * @return String
	// * @throws
	// */
	// @ExceptionHandler( BindException.class )
	// @ResponseStatus(value = HttpStatus.OK)
	// @ResponseBody
	// public String handleRequestException(HttpServletRequest request,
	// BindException ex) {
	// handleLog(request, ex);
	// ResultMessage message = new ResultMessage(
	// HttpStatus.BAD_REQUEST.value(), "parameter_bind_exception");
	// return JSONUtil.toJson(message);
	// }
	//
	// /**
	// * @Description 400 - Bad Request
	// * @param request
	// * @param ex
	// * @return String
	// * @throws
	// */
	// @ExceptionHandler(ConstraintViolationException.class)
	// @ResponseStatus(value = HttpStatus.OK)
	// @ResponseBody
	// public String handleRequestException(HttpServletRequest request,
	// ConstraintViolationException ex) {
	// handleLog(request, ex);
	// ResultMessage message = new ResultMessage(
	// HttpStatus.BAD_REQUEST.value(), "validation_exception");
	// return JSONUtil.toJson(message);
	// }
	//
	// /**
	// * @Description 400 - Bad Request
	// * @param request
	// * @param ex
	// * @return String
	// * @throws
	// */
	// @ExceptionHandler(ValidationException.class)
	// @ResponseStatus(value = HttpStatus.OK)
	// @ResponseBody
	// public String handleRequestException(HttpServletRequest request,
	// ValidationException ex) {
	// handleLog(request, ex);
	// ResultMessage message = new ResultMessage(
	// HttpStatus.BAD_REQUEST.value(), "validation_exception");
	// return JSONUtil.toJson(message);
	// }
	//
	// /**
	// * @Description 405 - Method Not Allowed
	// * @param request
	// * @param ex
	// * @return String
	// * @throws
	// */
	// @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	// @ResponseStatus(value = HttpStatus.OK)
	// @ResponseBody
	// public String handleRequestException(HttpServletRequest request,
	// HttpRequestMethodNotSupportedException ex) {
	// handleLog(request, ex);
	// ResultMessage message = new ResultMessage(
	// HttpStatus.METHOD_NOT_ALLOWED.value(),
	// "request_method_not_supported");
	// return JSONUtil.toJson(message);
	// }

	// /**
	// * @Description Unsupported Media Type
	// * @param request
	// * @param ex
	// * @return String
	// * @throws
	// */
	// @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	// @ResponseStatus(value = HttpStatus.OK)
	// @ResponseBody
	// public String handleRequestException(HttpServletRequest request,
	// HttpMediaTypeNotSupportedException ex) {
	// handleLog(request, ex);
	// ResultMessage message = new ResultMessage(
	// HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
	// "content_type_not_supported");
	// return JSONUtil.toJson(message);
	// }
	//


	/**
	 * 
	 * @Title handleSystemException
	 * @Description 系统错误
	 * @param request
	 * @param ex
	 * @return
	 * @throws JsonProcessingException
	 *             String
	 * @throws
	 */
	@ExceptionHandler(SystemException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public String handleSystemException(HttpServletRequest request, SystemException ex) {
		handleLog(request, ex);
		return showJson(HttpStatus.INTERNAL_SERVER_ERROR.value(), "", request);
	}

	/**
	 * 
	 * @Title handleAllException
	 * @Description json转换错误
	 * @param request
	 * @param ex
	 * @return String
	 * @throws
	 */
	@ExceptionHandler(JsonProcessingException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public String handleAllException(HttpServletRequest request, JsonProcessingException ex) {
		handleLog(request, ex);
		return showJson(500, "请联系客服人员service jsonException!", request);

	}

	private void handleLog(HttpServletRequest request, Exception ex) {
		StringBuffer logBuffer = new StringBuffer();
		if (request != null) {
			logBuffer.append("  request method=" + request.getMethod());
			logBuffer.append("  url=" + request.getRequestURL());
		}
		if (ex != null) {
			logBuffer.append("  exception:" + ex);
		}
		LOG.error(logBuffer.toString());
		if (LOG.isDebugEnabled()) {
			ex.printStackTrace();
		}
	}

	private String showJson(Integer code, String msg, HttpServletRequest request) {
		JSONObject json = new JSONObject();
		json.put("resultCode", code);
		json.put("resultMessage", msg);
		return json.toString();
	}
}
